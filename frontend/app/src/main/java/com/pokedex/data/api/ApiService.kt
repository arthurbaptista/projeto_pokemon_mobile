package com.pokedex.data.api

import com.pokedex.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("login/")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("cadastrar-usuario/")
    suspend fun registerUser(@Body request: UserRegistrationRequest): Response<LoginResponse>

    @GET("dashboard/")
    suspend fun getDashboard(): Response<DashboardResponse>

    @GET("pokemons/listar")
    suspend fun listPokemons(): Response<List<Pokemon>>

    @GET("pokemons/listar")
    suspend fun searchByType(@Query("tipo") type: String): Response<List<Pokemon>>

    @GET("pokemons/listar")
    suspend fun searchByAbility(@Query("habilidade") ability: String): Response<List<Pokemon>>

    @POST("pokemons/criar")
    suspend fun createPokemon(@Body request: PokemonRequest): Response<Map<String, String>>

    @GET("pokemons/{id}/")
    suspend fun getPokemonDetails(@Path("id") id: Int): Response<Pokemon>

    @PUT("pokemons/{id}/")
    suspend fun updatePokemon(@Path("id") id: Int, @Body request: PokemonUpdateRequest): Response<Map<String, String>>

    @DELETE("pokemons/{id}/")
    suspend fun deletePokemon(@Path("id") id: Int): Response<Map<String, String>>

}