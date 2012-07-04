from django.db import models

class Precompilation(models.Model):
    ser = models.FileField(upload_to="precompilation")
    module = models.CharField(max_length=1024)
    permcount = models.PositiveIntegerField()