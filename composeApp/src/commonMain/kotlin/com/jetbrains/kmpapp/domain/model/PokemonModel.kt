package com.jetbrains.kmpapp.domain.model

/**
 * coilを使ったgifアニメーションはAndroidのみサポートしている.
 * https://coil-kt.github.io/coil/gifs/
 */
internal data class PokemonModel(
    val id: Int,
    val name: String,
    val url: String,
    val frontDefaultPictureUrl: String,
    val backDefaultPictureUrl: String,
    val gitUrl: String,
    val criesLatestUrl: String,
)