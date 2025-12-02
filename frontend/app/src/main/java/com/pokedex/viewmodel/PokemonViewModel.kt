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

    // Usamos ApiResponse aqui só para manter compatibilidade com a Tela que espera success/message
    private val _pokemonList = MutableLiveData<ApiResponse<List<Pokemon>>>()
    val pokemonList: LiveData<ApiResponse<List<Pokemon>>> = _pokemonList

    private val _operationResult = MutableLiveData<ApiResponse<Unit>>()
    val operationResult: LiveData<ApiResponse<Unit>> = _operationResult

    private val _pokemonDetails = MutableLiveData<ApiResponse<Pokemon>>()
    val pokemonDetails: LiveData<ApiResponse<Pokemon>> = _pokemonDetails

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun listPokemons() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.listPokemons()
                if (response.isSuccessful) {
                    _pokemonList.value = ApiResponse(true, "Sucesso", response.body())
                } else {
                    _pokemonList.value = ApiResponse(false, "Erro ao listar", null)
                }
            } catch (e: Exception) {
                _pokemonList.value = ApiResponse(false, e.message ?: "Erro", null)
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
                if (response.isSuccessful) {
                    _operationResult.value = ApiResponse(true, "Criado com sucesso", null)
                } else {
                    _operationResult.value = ApiResponse(false, "Erro ao criar", null)
                }
            } catch (e: Exception) {
                _operationResult.value = ApiResponse(false, e.message ?: "Erro", null)
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
                if (response.isSuccessful) {
                    _pokemonDetails.value = ApiResponse(true, "Sucesso", response.body())
                } else {
                    _pokemonDetails.value = ApiResponse(false, "Erro", null)
                }
            } catch (e: Exception) {
                _pokemonDetails.value = ApiResponse(false, e.message ?: "Erro", null)
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
                if (response.isSuccessful) _operationResult.value = ApiResponse(true, "Atualizado", null)
                else _operationResult.value = ApiResponse(false, "Erro", null)
            } catch(e: Exception) {
                _operationResult.value = ApiResponse(false, e.message ?: "Erro", null)
            } finally { _isLoading.value = false }
        }
    }

    fun deletePokemon(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.deletePokemon(id)
                if (response.isSuccessful) _operationResult.value = ApiResponse(true, "Excluído", null)
                else _operationResult.value = ApiResponse(false, "Erro", null)
            } catch(e: Exception) {
                _operationResult.value = ApiResponse(false, e.message ?: "Erro", null)
            } finally { _isLoading.value = false }
        }
    }
}