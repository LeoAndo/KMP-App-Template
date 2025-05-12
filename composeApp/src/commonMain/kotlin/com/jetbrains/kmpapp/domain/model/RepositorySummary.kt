package com.jetbrains.kmpapp.domain.model

internal data class RepositorySummary(
    val id: Int,
    val name: String,
    val ownerName: String,
    val stargazersCount: Int,
    val forksCount: Int,
    val htmlUrl: String,
)