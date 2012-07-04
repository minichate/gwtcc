from django import forms
from uploads.models import Precompilation

class PrecompilationForm(forms.ModelForm):
    ser = forms.FileField()
    module = forms.CharField(max_length=1024)
    permcount = forms.IntegerField(min_value=0)
    
    class Meta:
        model = Precompilation