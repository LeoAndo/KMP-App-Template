package com.jetbrains.kmpapp.screens.githubsearch

import com.jetbrains.kmpapp.domain.model.RepositorySummary

internal sealed interface UiState {
    data object Initial : UiState
    data object Loading : UiState
    data class Failure(val throwable: Throwable) : UiState
    data class Success(val items: List<RepositorySummary>) : UiState
}