package com.pokedex.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokedex.data.model.DashboardResponse
import com.pokedex.data.repository.PokemonRepository
import kotlinx.coroutines.launch

// Criamos uma classe temporária para a tela usar, já com as listas de String prontas
data class DashboardUiState(
    val totalPokemon: Int,
    val top3Tipos: List<String>,
    val top3Habilidades: List<String>
)

class DashboardViewModel(private val repository: PokemonRepository) : ViewModel() {

    private val _dashboardData = MutableLiveData<DashboardUiState>()
    val dashboardData: LiveData<DashboardUiState> = _dashboardData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchDashboardData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getDashboard()
                if (response.isSuccessful && response.body() != null) {
                    val rawData = response.body()!!

                    // Converte os objetos do servidor em lista de nomes simples
                    val tipos = rawData.topTiposData.map { it["tipo"].toString() }
                    val habilidades = rawData.topHabilidadesData.map { it["nome"].toString() }

                    _dashboardData.value = DashboardUiState(
                        totalPokemon = rawData.totalPokemon,
                        top3Tipos = tipos,
                        top3Habilidades = habilidades
                    )
                } else {
                    _error.value = "Falha ao carregar dashboard: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Erro: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}