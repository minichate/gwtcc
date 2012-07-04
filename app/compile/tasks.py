from celery.task import task
from uploads.models import Precompilation
from compile.models import Permutation
from django.core.files import File
import subprocess
import os
import os.path
import tempfile
import shutil

@task()
def compile_permutation(key, perm):
    workDir = tempfile.mkdtemp()
    
    precomp = Precompilation.objects.get(pk=key)
    compiler_dir = workDir + '/' + precomp.module + '/compiler'
    
    os.makedirs(compiler_dir)
    
    with open(compiler_dir + "/precompilation.ser", 'wb+') as destination:
        for chunk in precomp.ser.chunks():
            destination.write(chunk)
    
    result = subprocess.call(['java', '-Xmx1024m', '-cp', '/Users/minichate/Documents/workspace/google-web-toolkit-read-only/build/lib/*', 'com.google.gwt.dev.CompilePerms', precomp.module, '-workDir', workDir, '-perms', str(perm)])
    
    permutation = Permutation.objects.create(compilation=precomp, permutation=perm, result=result)
    
    f = File(open('%s/permutation-%s.js' % (compiler_dir, perm)))
    permutation.js.save(os.path.basename('permutation-%s.js' % perm), f)
    permutation.save()
    
    shutil.rmtree(workDir)
    
    return permutation.pk