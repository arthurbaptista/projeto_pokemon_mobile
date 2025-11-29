import json
from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from BO.auth_bo import AuthBO

auth_bo = AuthBO()


@csrf_exempt
def login_view(request):
    if request.method == 'POST':
        try:
            data = json.loads(request.body)
            resultado = auth_bo.login(data.get('login'), data.get('senha'))
            return JsonResponse(resultado, status=200)
        except ValueError as e:
            return JsonResponse({'erro': str(e)}, status=401)
        except Exception:
            return JsonResponse({'erro': 'Erro interno'}, status=500)

    return JsonResponse({'erro': 'Método não permitido'}, status=405)


@csrf_exempt
def registrar_usuario_view(request):
    if request.method == 'POST':
        try:
            data = json.loads(request.body)
            login = data.get('login')
            senha = data.get('senha')

            resultado = auth_bo.criar_usuario(login, senha)

            return JsonResponse(resultado, status=201)

        except ValueError as e:
            return JsonResponse({'erro': str(e)}, status=400)
        except Exception as e:
            return JsonResponse({'erro': 'Erro interno ao cadastrar.'}, status=500)

    return JsonResponse({'erro': 'Método não permitido'}, status=405)