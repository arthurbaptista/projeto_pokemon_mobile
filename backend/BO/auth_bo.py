from django.contrib.auth import authenticate
from django.contrib.auth.models import User

class AuthBO:
    def login(self, username, password):
        user = authenticate(username=username, password=password)

        if user is not None:
            if not user.is_active:
                raise ValueError('Usuário inativo.')

            return {
                'mensagem': 'Login realizado com sucesso',
                'usuario': user.username,
                'id': user.id
            }
        else:
            raise ValueError('Login ou Senha incorretos')

    def criar_usuario(self, username, password):
        if not username or not password:
            raise ValueError('Login e Senha são obrigatórios.')

        if User.objects.filter(username=username).exists():
            raise ValueError('Este login já está em uso.')

        user = User.objects.create_user(username=username, password=password)

        return {
            'mensagem': 'Usuário cadastrado com sucesso!',
            'id': user.id,
            'usuario': user.username
        }