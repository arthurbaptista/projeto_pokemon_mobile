
package com.pokedex.data.model

data class PokemonRequest(
    val nome: String,
    val tipo: String,
    val habilidades: List<String>,
    val usuario_login: String
)
