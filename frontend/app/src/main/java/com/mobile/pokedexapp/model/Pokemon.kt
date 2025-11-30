package com.mobile.pokedexapp.model

import java.io.Serializable

data class Pokemon(
    val id: String? = null,
    val nome: String,
    val tipo: String,
    val habilidades: String,
    val usuarioCriador: String,
    val imagemUrl: String? = null
) : Serializable