from django.http import HttpResponse
from uploads.forms import PrecompilationForm
from compile.tasks import compile_permutation
from celery.task import group
from compile.models import Permutation
import zipfile
import tempfile
from django.core.servers.basehttp import FileWrapper

def handler(request):
    
    if request.method == 'POST':
        form = PrecompilationForm(request.POST, request.FILES)
        
        if form.is_valid():
            form.save()
            try:
                g = group([compile_permutation.subtask((form.instance.pk, i)) for i in xrange(form.instance.permcount)])
                res = g.apply_async()
                result_pks = res.join()
                
                results = [Permutation.objects.get(pk=x) for x in result_pks]
                
                temp = tempfile.TemporaryFile()
                archive = zipfile.ZipFile(temp, 'w', zipfile.ZIP_DEFLATED)
                
                for result in results:
                    archive.write(result.js.path, 'permutation-%s.js' % result.permutation)
                        
                archive.close()
                        
                wrapper = FileWrapper(temp)
                response = HttpResponse(wrapper, mimetype='application/zip')
                response['Content-Disposition'] = 'attachment; filename=permutations.zip'
                response['Content-Length'] = temp.tell()
                temp.seek(0)
                
                return response
                
            except Exception, e:
                print e
    
    return response
