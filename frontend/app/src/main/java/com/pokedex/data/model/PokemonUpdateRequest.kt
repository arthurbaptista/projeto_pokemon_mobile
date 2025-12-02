
package com.pokedex.data.model

data class PokemonUpdateRequest(
    val nome: String,
    val tipo: String,
    val habilidades: List<String>
)
