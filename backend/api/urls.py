# api/urls.py
from django.urls import path
from . import views, auth_view

urlpatterns = [
    path('login/', auth_view.login_view, name='login'),
    path('cadastrar-usuario/', auth_view.registrar_usuario_view, name='novo-usuario'),

    path('dashboard/', views.dashboard_view, name='dashboard'),
    path('pokemons/listar', views.listar_pokemons_view, name='pokemon-list'),
    path('pokemons/criar', views.pokemon_criar_view, name='pokemon-list'),

    path('pokemons/<int:pk>/', views.pokemon_view, name='pokemon-detail'),
]