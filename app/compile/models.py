from django.db import models
from uploads.models import Precompilation

class Permutation(models.Model):
    compilation = models.ForeignKey(Precompilation)
    js = models.FileField(upload_to="permutation")
    permutation = models.PositiveIntegerField()
    result = models.IntegerField()
    