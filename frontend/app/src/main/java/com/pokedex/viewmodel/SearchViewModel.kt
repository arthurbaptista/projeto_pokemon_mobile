package com.pokedex.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokedex.data.model.Pokemon
import com.pokedex.data.repository.PokemonRepository
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: PokemonRepository) : ViewModel() {

    private val _searchResult = MutableLiveData<List<Pokemon>?>()
    val searchResult: LiveData<List<Pokemon>?> = _searchResult

    private val _uniqueTypes = MutableLiveData<List<String>>()
    val uniqueTypes: LiveData<List<String>> = _uniqueTypes

    private val _uniqueAbilities = MutableLiveData<List<String>>()
    val uniqueAbilities: LiveData<List<String>> = _uniqueAbilities

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun searchPokemons(query: String, searchMode: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = when (searchMode) {
                    "TYPE" -> repository.searchByType(query)
                    "ABILITY" -> repository.searchByAbility(query)
                    else -> null
                }

                if (response != null && response.isSuccessful) {
                    _searchResult.value = response.body()
                } else {
                    _searchResult.value = emptyList()
                    _error.value = "Erro ao buscar Pokémon: ${response?.code()}"
                }
            } catch (e: Exception) {
                _searchResult.value = emptyList()
                _error.value = "Erro de conexão ao buscar Pokémon: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchAllUniqueData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. Call the "List All Pokemons" endpoint
                val response = repository.listPokemons()

                if (response.isSuccessful && response.body() != null) {
                    val allPokemons = response.body() ?: emptyList()

                    // 2. Extract unique types
                    val types = allPokemons
                        .map { it.tipo }
                        .filter { it.isNotEmpty() }
                        .distinct()
                        .sorted()
                    _uniqueTypes.value = types

                    // 3. Extract unique abilities
                    val abilities = allPokemons
                        .flatMap { it.habilidades }
                        .filter { it.isNotEmpty() }
                        .distinct()
                        .sorted()
                    _uniqueAbilities.value = abilities

                } else {
                    _error.value = "Erro ao carregar dados: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Erro de conexão ao carregar dados: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
