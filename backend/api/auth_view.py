from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework import status
import BO.auth_bo
from rest_framework.authtoken.models import Token
from django.contrib.auth.models import User

@api_view(['POST'])
def login_view(request):
    try:
        data = request.data
        # Chama seu BO para validar senha (pode manter sua lógica atual)
        resultado_bo = BO.auth_bo.AuthBO().login(data.get('login'), data.get('senha'))

        # Busca o usuário e gera/recupera o token
        user = User.objects.get(username=resultado_bo['usuario'])
        token, created = Token.objects.get_or_create(user=user)

        # Retorna o token junto com os dados
        return Response({
            'token': token.key,
            'usuario': user.username,
            'id': user.id
        }, status=status.HTTP_200_OK)

    except ValueError as e:
        return Response({'erro': str(e)}, status=status.HTTP_401_UNAUTHORIZED)


@api_view(['POST'])
def registrar_usuario_view(request):
    try:
        data = request.data
        login = data.get('login')
        senha = data.get('senha')

        resultado = BO.auth_bo.AuthBO().criar_usuario(login, senha)
        return Response(resultado, status=status.HTTP_201_CREATED)

    except ValueError as e:
        return Response({'erro': str(e)}, status=status.HTTP_400_BAD_REQUEST)
    except Exception as e:
        return Response({'erro': 'Erro interno ao cadastrar.'}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)