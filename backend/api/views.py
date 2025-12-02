from rest_framework.decorators import api_view, permission_classes
from rest_framework.response import Response
from rest_framework import status
from rest_framework.permissions import IsAuthenticated
from django.core.exceptions import ObjectDoesNotExist
import BO.pokemon_bo

@api_view(['GET'])
@permission_classes([IsAuthenticated])
def dashboard_view(request):
    dados = BO.pokemon_bo.PokemonBO().get_dashboard_data()
    return Response(dados)

@api_view(['GET'])
@permission_classes([IsAuthenticated])
def listar_pokemons_view(request):
    tipo = request.GET.get('tipo')
    habilidade = request.GET.get('habilidade')

    lista = BO.pokemon_bo.PokemonBO().listar_pokemons(tipo, habilidade)
    return Response(lista)

@api_view(['POST'])
@permission_classes([IsAuthenticated])
def pokemon_criar_view(request):
    try:
        resposta = BO.pokemon_bo.PokemonBO().criar_pokemon(request.data)
        return Response(resposta, status=status.HTTP_201_CREATED)

    except ValueError as e:
        return Response({'erro': str(e)}, status=status.HTTP_400_BAD_REQUEST)
    except Exception as e:
        return Response({'erro': str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

@api_view(['GET', 'PUT', 'DELETE'])
@permission_classes([IsAuthenticated])
def pokemon_view(request, pk):
    try:
        if request.method == 'GET':
            return Response(BO.pokemon_bo.PokemonBO().obter_pokemon(pk))

        elif request.method == 'PUT':
            # request.data substitui o json.loads
            resp = BO.pokemon_bo.PokemonBO().atualizar_pokemon(pk, request.data)
            return Response(resp)

        elif request.method == 'DELETE':
            resp = BO.pokemon_bo.PokemonBO().excluir_pokemon(pk)
            return Response(resp)

    except ValueError as e:
        return Response({'erro': str(e)}, status=status.HTTP_400_BAD_REQUEST)
    except ObjectDoesNotExist:
        return Response({'erro': 'Não encontrado'}, status=status.HTTP_404_NOT_FOUND)
    except Exception as e:
        return Response({'erro': str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)