package com.jetbrains.kmpapp.screens.pokemon

import com.jetbrains.kmpapp.domain.model.PokemonModel


internal data class UiState(
    val items: List<PokemonModel> = emptyList(),
    val currentOffset: Int = 0,
    val isLastPage: Boolean = false,
    val isLoading: Boolean = false,
    val throwable: Throwable? = null,
)