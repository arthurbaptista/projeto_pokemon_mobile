
package com.pokedex.data.model

data class DashboardResponse(
    val totalPokemon: Int,
    val top3Tipos: List<String>,
    val top3Habilidades: List<String>
)
