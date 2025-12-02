package com.pokedex.data.model

data class LoginResponse(
    val token: String?,
    val usuario: String?,
    val id: Int?,
    val erro: String?
)