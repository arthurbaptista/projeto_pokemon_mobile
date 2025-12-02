package com.pokedex.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokedex.data.model.LoginRequest
import com.pokedex.data.model.LoginResponse
import com.pokedex.data.model.UserRegistrationRequest
import com.pokedex.data.repository.PokemonRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: PokemonRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<LoginResponse>()
    val loginResult: LiveData<LoginResponse> = _loginResult

    private val _registerResult = MutableLiveData<LoginResponse>()
    val registerResult: LiveData<LoginResponse> = _registerResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(request: LoginRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.login(request)
                _loginResult.value = response.body()
            } catch (e: Exception) {
                _loginResult.value = LoginResponse(
                    success = false,
                    message = "Erro de conexão: ${e.message}",
                    token = null
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(request: UserRegistrationRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.registerUser(request)
                _registerResult.value = response.body()
            } catch (e: Exception) {
                _registerResult.value = LoginResponse(
                    success = false,
                    message = "Erro de conexão: ${e.message}",
                    token = null
                )
            } finally {
                _isLoading.value = false
            }
        }
    }
}

