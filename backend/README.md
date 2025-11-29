# Pokédex - Agenda Pokémon (Backend)

Este repositório contém o Backend (API) do projeto "Pokédex", desenvolvido para a disciplina de Desenvolvimento para Dispositivos Móveis. A API foi construída utilizando **Python** e **Django**, servindo dados para o aplicativo Android.

## 🚀 Tecnologias Utilizadas

* **Linguagem:** Python 3.8
* **Framework:** Django 4.2 (LTS)
* **Banco de Dados:** PostgreSQL
* **Arquitetura:** MVC com camada de Negócio separada (Business Object - BO)
* **Outros:** Django REST (apenas para CORS), Psycopg2.

## 📂 Estrutura do Projeto

* `api/`: Contém as **Views** (Controllers) e **Models** (Banco de Dados).
* `BO/`: Contém as Regras de Negócio (**Business Objects**).
    * `BO/bo.py`: Lógica principal de Pokémons.
    * `BO/auth_bo.py`: Lógica de autenticação.
    * `BO/scripts_dados/`: Scripts para popular o banco automaticamente.
* `setup/`: Configurações globais do Django (`settings.py`).

---

## ⚙️ Instalação e Configuração

Siga os passos abaixo para rodar o projeto localmente.

### 1. Pré-requisitos
Certifique-se de ter o **Python 3.8** e o **PostgreSQL** instalados.
Crie um banco de dados vazio no Postgres com o nome `pokedex_db`.

### 2. Clonar e Configurar Ambiente
```bash
# Clone o repositório
git clone <link-do-seu-repo>
cd backend

# Crie o ambiente virtual
python -m venv venv

# Ative o ambiente
# Windows:
.\venv\Scripts\activate
# Linux/Mac:
source venv/bin/activate


pip install -r requirements.txt (Instalar bibliotecas)

python manage.py migrate (Criar as tabelas vazias no banco dele)

python popular_banco.py (Esse comando vai criar os 3 usuarios e os 10 pokemons automaticamente).

python manage.py runserver