import os
import sys
import django
from pathlib import Path

BASE_DIR = Path(__file__).resolve().parent.parent.parent
sys.path.append(str(BASE_DIR))

# Configura o Django
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'setup.settings')
django.setup()

from django.contrib.auth.models import User
from api.models import Pokemon, Habilidade


def popular():
    print(f"--- Iniciando População do Banco (Rodando em: {BASE_DIR}) ---")

    # 1. CRIAR USUÁRIOS (Requisito: 3 usuários)
    usuarios = ['usuario1', 'usuario2', 'usuario3']
    user_objs = []

    print("Criando usuários...")
    for nome in usuarios:
        if not User.objects.filter(username=nome).exists():
            # Cria superuser
            u = User.objects.create_superuser(nome, '', '1234')
            print(f" -> Usuário criado: {nome} (Senha: 1234)")
            user_objs.append(u)
        else:
            print(f" -> Usuário já existe: {nome}")
            user_objs.append(User.objects.get(username=nome))

    dono = user_objs[0]  # Usa o primeiro usuário como dono dos pokemons

    # 2. CRIAR HABILIDADES
    print("\nCriando habilidades...")
    habilidades_lista = [
        "Choque do Trovão", "Cauda de Ferro", "Lança-Chamas", "Hidro Bomba",
        "Chicote de Vinha", "Investida", "Mordida", "Vento Cortante",
        "Raio Solar", "Pó do Sono"
    ]

    for h in habilidades_lista:
        Habilidade.objects.get_or_create(nome=h, status=True)
    print(" -> Habilidades cadastradas.")

    # 3. CRIAR 10 POKÉMONS
    print("\nCriando Pokémons...")
    pokemons_data = [
        ("Pikachu", "Elétrico", ["Choque do Trovão", "Cauda de Ferro"]),
        ("Charmander", "Fogo", ["Lança-Chamas", "Mordida"]),
        ("Squirtle", "Água", ["Hidro Bomba", "Investida"]),
        ("Bulbasaur", "Planta", ["Chicote de Vinha", "Raio Solar"]),
        ("Pidgey", "Voador", ["Vento Cortante", "Investida"]),
        ("Rattata", "Normal", ["Mordida", "Investida"]),
        ("Butterfree", "Inseto", ["Pó do Sono", "Vento Cortante"]),
        ("Jigglypuff", "Fada", ["Investida"]),
        ("Meowth", "Normal", ["Mordida"]),
        ("Psyduck", "Água", ["Hidro Bomba"])
    ]

    for nome, tipo, habs in pokemons_data:
        if not Pokemon.objects.filter(nome=nome, status=True).exists():
            p = Pokemon.objects.create(
                nome=nome,
                tipo=tipo,
                criado_por=dono,
                status=True
            )
            for h_nome in habs:
                h_obj = Habilidade.objects.get(nome=h_nome)
                p.habilidades.add(h_obj)
            print(f" -> Pokémon criado: {nome}")
        else:
            print(f" -> Pokémon já existe: {nome}")

    print("\n--- BANCO POPULADO COM SUCESSO! ---")


if __name__ == '__main__':
    popular()