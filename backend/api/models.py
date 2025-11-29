from django.db import models
from django.contrib.auth.models import User


class Habilidade(models.Model):
    nome = models.CharField(max_length=100)
    status = models.BooleanField(default=True)

    def __str__(self):
        return self.nome


class Pokemon(models.Model):
    nome = models.CharField(max_length=100)
    tipo = models.CharField(max_length=50)
    habilidades = models.ManyToManyField(Habilidade)
    criado_por = models.ForeignKey(User, on_delete=models.CASCADE)

    status = models.BooleanField(default=True)
    criado_em = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return self.nome