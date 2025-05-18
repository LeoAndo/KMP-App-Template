package com.jetbrains.kmpapp.domain.model

import com.jetbrains.kmpapp.data.pokemon.getBackDefaultPictureUrl

internal data class PokemonModel(
    val id: Int,
    val name: String,
    val url: String,
    val frontDefaultPictureUrl: String,
    val backDefaultPictureUrl: String,
    val gitUrl: String
)