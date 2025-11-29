from django.contrib.auth.models import User
from django.db.models import Count
from django.core.exceptions import ObjectDoesNotExist
from api.models import Pokemon, Habilidade


class PokemonBO:

    def get_dashboard_data(self):
        qs_status = Pokemon.objects.filter(status=True)
        total = qs_status.count()

        top_tipos = list(qs_status.values('tipo')
                         .annotate(qtd=Count('tipo'))
                         .order_by('-qtd')[:3])

        top_habs = list(Habilidade.objects.filter(status=True, pokemon__ativo=True)
                        .values('nome')
                        .annotate(qtd=Count('pokemon'))
                        .order_by('-qtd')[:3])

        return {
            'total_pokemons': total,
            'top_tipos': top_tipos,
            'top_habilidades': top_habs
        }

    def listar_pokemons(self, tipo=None, habilidade=None):
        # Requisito[cite: 28]: Mostrar lista de pokemons
        pokemons = Pokemon.objects.filter(status=True)

        # Pesquisa com Like (icontains)
        if tipo:
            pokemons = pokemons.filter(tipo__icontains=tipo)
        if habilidade:
            pokemons = pokemons.filter(habilidades__nome__icontains=habilidade)

        lista_resposta = []
        for p in pokemons:
            lista_resposta.append({
                'id': p.id,
                'nome': p.nome,
                'tipo': p.tipo,
                'habilidades': [h.nome for h in p.habilidades.all()],
                'criado_por': p.criado_por.username
            })
        return lista_resposta

    def criar_pokemon(self, dados_json):
        nome = dados_json.get('nome')
        tipo = dados_json.get('tipo')
        habs_nomes = dados_json.get('habilidades', [])
        usuario_login = dados_json.get('usuario_login')

        if Pokemon.objects.filter(nome=nome, ativo=True).exists():
            raise ValueError('Erro: Pokémon já existe.')

        if len(habs_nomes) < 1 or len(habs_nomes) > 3:
            raise ValueError('Erro: Mínimo 1 e máximo 3 habilidades.')

        try:
            usuario = User.objects.get(username=usuario_login)
        except User.DoesNotExist:
            raise ValueError('Usuário não encontrado.')

        novo_pokemon = Pokemon.objects.create(
            nome=nome, tipo=tipo, criado_por=usuario, ativo=True
        )

        for h_nome in habs_nomes:
            hab_obj, _ = Habilidade.objects.get_or_create(nome=h_nome)
            novo_pokemon.habilidades.add(hab_obj)

        return {'mensagem': 'Cadastro com Sucesso'}

    def obter_pokemon(self, pk):
        try:
            p = Pokemon.objects.get(pk=pk, ativo=True)
            return {
                'id': p.id, 'nome': p.nome, 'tipo': p.tipo,
                'habilidades': [h.nome for h in p.habilidades.all()],
                'criado_por': p.criado_por.username
            }
        except Pokemon.DoesNotExist:
            raise ObjectDoesNotExist('Pokémon não encontrado')

    def atualizar_pokemon(self, pk, dados_json):
        try:
            pokemon = Pokemon.objects.get(pk=pk, ativo=True)
        except Pokemon.DoesNotExist:
            raise ObjectDoesNotExist('Pokémon não encontrado')

        if 'nome' in dados_json:
            # Valida duplicidade na edição
            novo_nome = dados_json['nome']
            if Pokemon.objects.filter(nome=novo_nome, ativo=True).exclude(pk=pk).exists():
                raise ValueError('Nome já utilizado por outro Pokémon.')
            pokemon.nome = novo_nome

        if 'tipo' in dados_json: pokemon.tipo = dados_json['tipo']

        if 'habilidades' in dados_json:
            habs_nomes = dados_json['habilidades']
            if len(habs_nomes) < 1 or len(habs_nomes) > 3:
                raise ValueError('Mínimo 1 e máximo 3 habilidades.')

            pokemon.habilidades.clear()
            for h_nome in habs_nomes:
                hab_obj, _ = Habilidade.objects.get_or_create(nome=h_nome)
                pokemon.habilidades.add(hab_obj)

        pokemon.save()
        return {'mensagem': 'Atualizado com sucesso'}

    def excluir_pokemon(self, pk):
        try:
            pokemon = Pokemon.objects.get(pk=pk, ativo=True)
            pokemon.status = False
            pokemon.save()
            return {'mensagem': 'Excluído com sucesso'}
        except Pokemon.DoesNotExist:
            raise ObjectDoesNotExist('Pokémon não encontrado')