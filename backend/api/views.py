import json
from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from django.core.exceptions import ObjectDoesNotExist
import BO.pokemon_bo

def dashboard_view(request):
    if request.method == 'GET':
        dados = BO.pokemon_bo.PokemonBO().get_dashboard_data()
        return JsonResponse(dados)
    return None

@csrf_exempt
def listar_pokemons_view(request):
    if request.method == 'GET':
        return JsonResponse(BO.pokemon_bo.PokemonBO().listar_pokemons(request.GET.get('tipo'),
                                                                      request.GET.get('habilidade')), safe=False)
    return None

@csrf_exempt
def pokemon_criar_view(request):
    if request.method == 'POST':
        try:
            data = json.loads(request.body)
            resposta = BO.pokemon_bo.PokemonBO().criar_pokemon(data)
            return JsonResponse(resposta, status=201)
        except ValueError as e:
            return JsonResponse({'erro': str(e)}, status=400)
        except Exception as e:
            return JsonResponse({'erro': str(e)}, status=500)

    return None

@csrf_exempt
def pokemon_view(request, pk):
    if request.method == 'GET':
        try:
            return JsonResponse(BO.pokemon_bo.PokemonBO().obter_pokemon(pk))
        except ObjectDoesNotExist:
            return JsonResponse({'erro': 'Não encontrado'}, status=404)

    elif request.method == 'PUT':
        try:
            data = json.loads(request.body)
            resp = BO.pokemon_bo.PokemonBO().atualizar_pokemon(pk, data)
            return JsonResponse(resp)
        except ValueError as e:
            return JsonResponse({'erro': str(e)}, status=400)
        except ObjectDoesNotExist:
            return JsonResponse({'erro': 'Não encontrado'}, status=404)

    elif request.method == 'DELETE':
        try:
            resp = BO.pokemon_bo.PokemonBO().excluir_pokemon(pk)
            return JsonResponse(resp)
        except ObjectDoesNotExist:
            return JsonResponse({'erro': 'Não encontrado'}, status=404)

    return None