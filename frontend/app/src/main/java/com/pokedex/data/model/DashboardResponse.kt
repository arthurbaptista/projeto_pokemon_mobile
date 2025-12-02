package com.pokedex.data.model

import com.google.gson.annotations.SerializedName

data class DashboardResponse(
    @SerializedName("total_pokemons")
    val totalPokemon: Int,

    @SerializedName("top_tipos")
    val topTiposData: List<Map<String, Any>>,

    @SerializedName("top_habilidades")
    val topHabilidadesData: List<Map<String, Any>>
)