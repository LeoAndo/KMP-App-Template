package com.jetbrains.kmpapp.screens.githubsearch.paging

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetbrains.kmpapp.data.githubsearch.GithubApi
import com.jetbrains.kmpapp.data.githubsearch.toModels
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

internal class GithubSearchPagingViewModel(private val api: GithubApi) : ViewModel() {
    var uiState by mutableStateOf(UiState())
        private set

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        uiState = uiState.copy(throwable = throwable, isLoading = false)
    }

    fun searchRepositories(query: String, page: Int, sort: String) {
        // 通信処理中の場合は、何もしない
        if (uiState.isLoading) return

        val isRefresh = page == 1
        if (isRefresh) {
            uiState = uiState.copy(items = emptyList())
        }

        viewModelScope.launch(coroutineExceptionHandler) {
            uiState = uiState.copy(isLoading = true, throwable = null)
            val response = api.searchRepositories(query, page, sort)
            val totalCount = response.total_count // 取得したリポジトリの総数
            val repositories = response.toModels()
            val newItems = if (isRefresh) {
                repositories
            } else {
                uiState.items + repositories
            }

            // _items.valueのサイズがtotalCount以上の場合、isLastPageをtrueにする
            val isLastPage = newItems.size >= totalCount

            uiState = uiState.copy(
                items = newItems,
                currentPage = page,
                isLastPage = isLastPage,
                isLoading = false,
                throwable = null,
            )
        }
    }

    fun loadNextPage(query: String, sort: String) {
        // すでに最後のページに到達している場合は、何もしない
        if (uiState.isLastPage) return

        val nextPage = uiState.currentPage + 1 // 次のページ番号
        searchRepositories(query, nextPage, sort)
    }
}
