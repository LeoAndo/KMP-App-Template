package com.jetbrains.kmpapp.data.pokemon

import com.jetbrains.kmpapp.data.pokemon.PokemonResponse.PokemonResult
import com.jetbrains.kmpapp.domain.model.PokemonModel
import kotlinx.serialization.Serializable

/**
 * @param count The total number of Pokemon. ex) 1302
 * @param next The URL for the next page of results. ex) "https://pokeapi.co/api/v2/pokemon?offset=201&limit=200"
 * @param previous The URL for the previous page of results. ex) "https://pokeapi.co/api/v2/pokemon?offset=0&limit=1"
 * @param results The list of Pokemon results. ex) [PokemonResult(name="ivysaur", url="https://pokeapi.co/api/v2/pokemon/2/"), ...]
 */
@Serializable
internal data class PokemonResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<PokemonResult>
) {
    /**
     * @param name The name of the Pokemon. ex) "ivysaur"
     * @param url The URL of the Pokemon. ex) "https://pokeapi.co/api/v2/pokemon/2/"
     */
    @Serializable
    internal data class PokemonResult(val name: String, val url: String)
}

internal fun List<PokemonResult>.toModels(): List<PokemonModel> {
    return this.map { result ->
        PokemonModel(
            id = result.url.extractId(),
            name = result.name,
            url = result.url,
            frontDefaultPictureUrl = result.url.getFrontDefaultPictureUrl(),
            backDefaultPictureUrl = result.url.getBackDefaultPictureUrl(),
            gitUrl = result.url.getGifUrl(),
            criesLatestUrl = result.url.getCriesLatestUrl()
        )
    }
}

/**
 * Extract the ID from the URL.
 *
 * before "https://pokeapi.co/api/v2/pokemon/2/"
 * after "2"
 */
internal fun String.extractId() = this.substringAfter("pokemon").replace("/", "").toInt()
internal fun String.getFrontDefaultPictureUrl(): String =
    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${extractId()}.png"

internal fun String.getBackDefaultPictureUrl(): String =
    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/back/${extractId()}.png"

internal fun String.getGifUrl(): String =
    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/versions/generation-v/black-white/animated/${extractId()}.gif"

internal fun String.getCriesLatestUrl(): String = "https://raw.githubusercontent.com/PokeAPI/cries/main/cries/pokemon/latest/${extractId()}.ogg"