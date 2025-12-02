
package com.pokedex.data.model

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String?
)
