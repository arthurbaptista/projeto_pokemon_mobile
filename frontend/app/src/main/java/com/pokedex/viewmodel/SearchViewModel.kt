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

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

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
                }
            } catch (e: Exception) {
                _searchResult.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}