package com.mobile.pokedexapp.network

import com.exemplo.pokedex.model.LoginRequest
import com.exemplo.pokedex.model.Pokemon
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("login")
    fun login(@Body request: LoginRequest): Call<Map<String, Boolean>>

    @GET("pokemons")
    fun listarTodos(): Call<List<Pokemon>>

    @POST("pokemons")
    fun cadastrar(@Body pokemon: Pokemon): Call<Void>

    // Adicione os outros métodos (search, delete, update) conforme precisar
}