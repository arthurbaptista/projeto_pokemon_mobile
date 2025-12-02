
package com.pokedex.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokedex.data.model.ApiResponse
import com.pokedex.data.model.Pokemon
import com.pokedex.data.model.PokemonRequest
import com.pokedex.data.model.PokemonUpdateRequest
import com.pokedex.data.repository.PokemonRepository
import kotlinx.coroutines.launch

class PokemonViewModel(private val repository: PokemonRepository) : ViewModel() {

    private val _pokemonList = MutableLiveData<ApiResponse<List<Pokemon>>>()
    val pokemonList: LiveData<ApiResponse<List<Pokemon>>> = _pokemonList

    private val _pokemonDetails = MutableLiveData<ApiResponse<Pokemon>>()
    val pokemonDetails: LiveData<ApiResponse<Pokemon>> = _pokemonDetails

    private val _operationResult = MutableLiveData<ApiResponse<*>>()
    val operationResult: LiveData<ApiResponse<*>> = _operationResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun listPokemons() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.listPokemons()
                _pokemonList.value = response.body()
            } catch (e: Exception) {
                _pokemonList.value = ApiResponse(false, "Erro de conexão: ${e.message}", null)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getPokemonDetails(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getPokemonDetails(id)
                _pokemonDetails.value = response.body()
            } catch (e: Exception) {
                _pokemonDetails.value = ApiResponse(false, "Erro de conexão: ${e.message}", null)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createPokemon(request: PokemonRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.createPokemon(request)
                _operationResult.value = response.body()
            } catch (e: Exception) {
                _operationResult.value = ApiResponse<Unit>(false, "Erro de conexão: ${e.message}", null)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updatePokemon(id: Int, request: PokemonUpdateRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.updatePokemon(id, request)
                _operationResult.value = response.body()
            } catch (e: Exception) {
                _operationResult.value = ApiResponse<Unit>(false, "Erro de conexão: ${e.message}", null)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deletePokemon(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.deletePokemon(id)
                _operationResult.value = response.body()
            } catch (e: Exception) {
                _operationResult.value = ApiResponse<Unit>(false, "Erro de conexão: ${e.message}", null)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
