package com.jetbrains.kmpapp.screens.githubsearch.paging

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jetbrains.kmpapp.domain.exception.AppException
import com.jetbrains.kmpapp.launchExternalBrowser
import com.jetbrains.kmpapp.screens.component.AppSurface
import com.jetbrains.kmpapp.screens.component.EmptyScreenContent
import com.jetbrains.kmpapp.screens.component.AppLoading
import com.jetbrains.kmpapp.screens.githubsearch.SortType
import org.koin.compose.viewmodel.koinViewModel

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import com.jetbrains.kmpapp.screens.component.AppAlertDialog

@Composable
internal fun GithubSearchPagingScreen(modifier: Modifier = Modifier) {
    var query by rememberSaveable { mutableStateOf("") }
    var sortType by rememberSaveable { mutableStateOf(SortType.STARS) }
    var expanded by rememberSaveable { mutableStateOf(false) }
    val viewModel = koinViewModel<GithubSearchPagingViewModel>()
    val listState = rememberLazyListState()

    // ja) リストの最後までスクロールしたときに、最後のアイテムが読み込まれない問題のワークアラウンドです。
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItemIndex ->
                // ja) 最後のアイテムが表示されている場合、次のページを読み込みます。
                val totalItems = listState.layoutInfo.totalItemsCount
                if (lastVisibleItemIndex == totalItems - 1) {
                    viewModel.loadNextPage(query, sortType.sort)
                }
            }
    }

    GithubSearchPagingScreenStateless(
        modifier = modifier
            .fillMaxSize()
            .padding(WindowInsets.safeDrawing.asPaddingValues())
            .padding(12.dp),
        query = query,
        sortType = sortType,
        uiState = viewModel.uiState,
        expanded = expanded,
        onValueChange = { query = it },
        onSortTypeChange = { sortType = it },
        onSearch = { viewModel.searchRepositories(query, 1, sortType.sort) },
        onClickItem = { url -> launchExternalBrowser(url) },
        onDropdownMenuExpanded = { expanded = it },
        listState = listState
    )
}

@Composable
private fun GithubSearchPagingScreenStateless(
    modifier: Modifier = Modifier,
    query: String,
    sortType: SortType,
    uiState: UiState,
    expanded: Boolean,
    onValueChange: (String) -> Unit,
    onSortTypeChange: (SortType) -> Unit,
    onSearch: () -> Unit,
    onClickItem: (String) -> Unit = {},
    onDropdownMenuExpanded: (Boolean) -> Unit,
    listState: LazyListState
) {
    AppSurface {
        Column(modifier = modifier) {
            OutlinedTextField(
                value = query,
                label = {
                    Text(text = "Search GitHub Repositories")
                },
                singleLine = true,
                maxLines = 1,
                onValueChange = { onValueChange(it) },
                modifier = Modifier.fillMaxWidth(),
                isError = query.isEmpty()
            )
            Spacer(modifier = Modifier.size(8.dp))

            Box {
                OutlinedTextField(
                    value = sortType.sort,
                    label = { Text(text = "Sort by") },
                    singleLine = true,
                    maxLines = 1,
                    trailingIcon = {
                        IconButton(onClick = { onDropdownMenuExpanded(true) }) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = "Sort by",
                            )
                        }
                    },
                    onValueChange = { },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { onDropdownMenuExpanded(false) }) {
                    SortType.entries.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.sort) },
                            onClick = {
                                onSortTypeChange(type)
                                onDropdownMenuExpanded(false)
                            }
                        )
                        if (type != SortType.entries.last()) {
                            HorizontalDivider()
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.size(8.dp))

            Button(
                onClick = { onSearch() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Search")
            }

            LazyColumn(state = listState) {
                items(items = uiState.items) {
                    it.let { item ->
                        Card(
                            modifier = Modifier.clickable { onClickItem(item.htmlUrl) }
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(12.dp),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    "ownerName: ${item.ownerName}",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    "repositoryName: ${item.name}",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    "stargazersCount: ${item.stargazersCount}",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    "forksCount: ${item.forksCount}",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        Spacer(modifier = Modifier.size(8.dp))
                    }
                }
            }

            if (uiState.isLoading) {
                AppLoading(
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (uiState.throwable == null && uiState.items.isEmpty()) {
                EmptyScreenContent(modifier = Modifier.fillMaxSize())
            }

            if (uiState.throwable != null) {
                val errorMessage =
                    uiState.throwable.message ?: "error occurred. Please try again later."
                val message = if (uiState.throwable is AppException) {
                    when (uiState.throwable) {
                        is AppException.Forbidden -> "Please wait a moment and try again as you have reached the request limit."
                        is AppException.UnAuthorized -> "Unauthorized access. Please check your credentials."
                        is AppException.Network -> "Network error occurred. Please check your network connection."
                        else -> errorMessage
                    }
                } else {
                    errorMessage
                }
                AppAlertDialog(
                    titleText = "Error",
                    messageText = message,
                    confirmText = "OK",
                )
            }
        }
    }
}
