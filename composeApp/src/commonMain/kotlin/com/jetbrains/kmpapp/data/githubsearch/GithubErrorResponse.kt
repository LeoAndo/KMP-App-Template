package com.jetbrains.kmpapp.data.githubsearch

import kotlinx.serialization.Serializable

@Serializable
internal data class GithubErrorResponse(val documentation_url: String, val message: String)