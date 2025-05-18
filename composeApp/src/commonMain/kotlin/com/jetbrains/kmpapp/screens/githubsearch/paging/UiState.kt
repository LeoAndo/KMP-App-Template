package com.jetbrains.kmpapp.screens.githubsearch.paging

import com.jetbrains.kmpapp.domain.model.RepositorySummary

internal data class UiState(
    val items: List<RepositorySummary> = emptyList(),
    val currentPage: Int = 1,
    val isLastPage: Boolean = false,
    val isLoading: Boolean = false,
    val throwable: Throwable? = null,
)