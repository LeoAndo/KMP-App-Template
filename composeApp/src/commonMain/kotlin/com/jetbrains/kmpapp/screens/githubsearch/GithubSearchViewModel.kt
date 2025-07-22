package com.jetbrains.kmpapp.screens.githubsearch

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetbrains.kmpapp.data.githubsearch.GithubApi
import com.jetbrains.kmpapp.data.githubsearch.toModels
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

internal class GithubSearchViewModel(private val api: GithubApi) : ViewModel() {
    var uiState by mutableStateOf<UiState>(UiState.Initial)
        private set
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        uiState = UiState.Failure(throwable)
    }

    fun searchRepositories(query: String, page: Int, sort: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            uiState = UiState.Loading
            val searchRepositories = api.searchRepositories(query, page, sort).toModels()
            uiState = UiState.Success(searchRepositories)
        }
    }
}
