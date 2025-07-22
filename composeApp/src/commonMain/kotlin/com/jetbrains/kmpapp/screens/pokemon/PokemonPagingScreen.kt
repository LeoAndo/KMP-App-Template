package com.jetbrains.kmpapp.screens.pokemon

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jetbrains.kmpapp.domain.exception.AppException
import com.jetbrains.kmpapp.launchExternalBrowser
import com.jetbrains.kmpapp.screens.component.EmptyScreenContent
import com.jetbrains.kmpapp.screens.component.AppLoading
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.jetbrains.kmpapp.screens.component.AppAlertDialog

@Composable
internal fun PokemonPagingScreen() {
    val viewModel = koinViewModel<PokemonPagingViewModel>()
    val listState = rememberLazyGridState()

    // ja) リストの最後までスクロールしたときに、最後のアイテムが読み込まれない問題のワークアラウンドです。
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItemIndex ->
                // ja) 最後のアイテムが表示されている場合、次のページを読み込みます。
                val totalItems = listState.layoutInfo.totalItemsCount
                if (lastVisibleItemIndex == totalItems - 1) {
                    viewModel.loadNextPage()
                }
            }
    }

    PokemonPagingScreenStateless(
        modifier = Modifier.fillMaxSize()
            .padding(WindowInsets.safeDrawing.asPaddingValues()).padding(12.dp),
        uiState = viewModel.uiState,
        onSearch = { viewModel.fetchPokemon(0) },
        onClickItem = { url -> launchExternalBrowser(url) },
        listState = listState
    )
}

@Composable
private fun PokemonPagingScreenStateless(
    modifier: Modifier = Modifier,
    uiState: UiState,
    onSearch: () -> Unit,
    onClickItem: (String) -> Unit,
    listState: LazyGridState
) {
    Column(modifier = modifier) {
        Button(
            onClick = { onSearch() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "fetch")
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(180.dp),
            state = listState,
        ) {
            items(uiState.items, key = { it.id }) { item ->
                Column(
                    Modifier
                        .padding(8.dp)
                        .clickable { onClickItem(item.gitUrl) }
                ) {
                    AsyncImage(
                        model = item.frontDefaultPictureUrl,
                        contentDescription = item.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    )
                    Spacer(Modifier.height(2.dp))

                    Text(item.name, style = MaterialTheme.typography.titleMedium)
                    Text("${item.id}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        if (uiState.isLoading) {
            AppLoading(
                modifier = Modifier.fillMaxSize()
            )
        }

        if (uiState.throwable == null && uiState.items.isEmpty()) {
            EmptyScreenContent(modifier = Modifier.fillMaxSize())
        }

        if (uiState.throwable != null) {
            val message = uiState.throwable.message
            AppAlertDialog(titleText = "Error", messageText = message, confirmText = "OK")
        }
    }
}
