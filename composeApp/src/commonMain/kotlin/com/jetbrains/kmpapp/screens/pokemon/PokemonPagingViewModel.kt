package com.jetbrains.kmpapp.screens.pokemon

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetbrains.kmpapp.data.pokemon.PokemonApi
import com.jetbrains.kmpapp.data.pokemon.PokemonApi.Companion.LIMIT
import com.jetbrains.kmpapp.data.pokemon.toModels
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

internal class PokemonPagingViewModel(private val api: PokemonApi) : ViewModel() {
    var uiState by mutableStateOf(UiState())
        private set

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        uiState = uiState.copy(throwable = throwable, isLoading = false)
    }

    fun fetchPokemon(offset: Int) {
        // 通信処理中の場合は、何もしない
        if (uiState.isLoading) return

        val isRefresh = offset == 0
        if (isRefresh) {
            uiState = uiState.copy(items = emptyList())
        }

        viewModelScope.launch(coroutineExceptionHandler) {
            uiState = uiState.copy(isLoading = true, throwable = null)
            val response = api.fetchPokemon(offset)
            val totalCount = response.count // ポケモンの総数
            val pokemonModels = response.results.toModels() // 取得したポケモンのリスト
            val newItems = if (isRefresh) {
                pokemonModels
            } else {
                uiState.items + pokemonModels
            }

            // _items.valueのサイズがtotalCount以上の場合、isLastPageをtrueにする
            val isLastPage = newItems.size >= totalCount

            uiState = uiState.copy(
                items = newItems,
                currentOffset = offset,
                isLastPage = isLastPage,
                isLoading = false,
                throwable = null,
            )
        }
    }

    fun loadNextPage() {
        // すでに最後のページに到達している場合は、何もしない
        if (uiState.isLastPage) return

        val nextPage = uiState.currentOffset + LIMIT // 次のページ番号
        fetchPokemon(offset = nextPage)
    }
}
