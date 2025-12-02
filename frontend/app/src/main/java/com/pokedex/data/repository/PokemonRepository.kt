
package com.pokedex.data.repository

import com.pokedex.data.api.ApiService
import com.pokedex.data.model.LoginRequest
import com.pokedex.data.model.PokemonRequest
import com.pokedex.data.model.PokemonUpdateRequest
import com.pokedex.data.model.UserRegistrationRequest

class PokemonRepository(private val apiService: ApiService) {

    suspend fun login(request: LoginRequest) = apiService.login(request)

    suspend fun registerUser(request: UserRegistrationRequest) = apiService.registerUser(request)

    suspend fun getDashboard() = apiService.getDashboard()

    suspend fun listPokemons() = apiService.listPokemons()

    suspend fun searchByType(type: String) = apiService.searchByType(type)

    suspend fun searchByAbility(ability: String) = apiService.searchByAbility(ability)

    suspend fun createPokemon(request: PokemonRequest) = apiService.createPokemon(request)

    suspend fun getPokemonDetails(id: Int) = apiService.getPokemonDetails(id)

    suspend fun updatePokemon(id: Int, request: PokemonUpdateRequest) = apiService.updatePokemon(id, request)

    suspend fun deletePokemon(id: Int) = apiService.deletePokemon(id)
}
