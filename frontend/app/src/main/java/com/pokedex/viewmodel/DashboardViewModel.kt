package com.pokedex.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokedex.data.model.DashboardResponse
import com.pokedex.data.repository.PokemonRepository
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: PokemonRepository) : ViewModel() {

    private val _dashboardData = MutableLiveData<DashboardResponse>()
    val dashboardData: LiveData<DashboardResponse> = _dashboardData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchDashboardData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getDashboard()
                if (response.isSuccessful && response.body()?.success == true) {
                    _dashboardData.value = response.body()?.data
                } else {
                    _error.value = response.body()?.message ?: "Falha ao carregar os dados do dashboard."
                }
            } catch (e: Exception) {
                _error.value = "Erro de conexão: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

