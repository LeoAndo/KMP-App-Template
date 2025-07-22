package com.jetbrains.kmpapp.screens.githubsearch

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
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
import com.jetbrains.kmpapp.screens.component.AppAlertDialog
import com.jetbrains.kmpapp.screens.component.EmptyScreenContent
import com.jetbrains.kmpapp.screens.component.AppLoading
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun GithubSearchScreen() {
    var query by rememberSaveable { mutableStateOf("") }
    var sortType by rememberSaveable { mutableStateOf(SortType.STARS) }
    var expanded by rememberSaveable { mutableStateOf(false) }
    val viewModel = koinViewModel<GithubSearchViewModel>()
    SearchScreenStateless(
        modifier = Modifier.fillMaxSize().padding(WindowInsets.safeDrawing.asPaddingValues())
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
    )
}

// Preview対象のComposeには第一引数にmodifier: Modifier = Modifierを指定する運用で。
@Composable
private fun SearchScreenStateless(
    modifier: Modifier = Modifier,
    query: String,
    sortType: SortType,
    uiState: UiState,
    expanded: Boolean,
    onValueChange: (String) -> Unit,
    onSortTypeChange: (SortType) -> Unit,
    onSearch: () -> Unit,
    onClickItem: (String) -> Unit,
    onDropdownMenuExpanded: (Boolean) -> Unit,
) {
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

        when (uiState) {
            UiState.Initial -> EmptyScreenContent(Modifier.fillMaxSize())
            UiState.Loading -> AppLoading(modifier = Modifier.fillMaxSize())
            is UiState.Failure -> {
                // 画面側で想定しないエラーに関しては予期しないエラーとしてメッセージ表示する
                val message = uiState.throwable.message
                // エラーの種類によって振る舞いを変えるパターン
                /*
                val message = when (val throwable = uiState.throwable) {
                    is AppException.Forbidden, is AppException.UnAuthorized, is AppException.Network -> {
                        // 何かしらの処理
                    }
                    else -> {
                        // それ以外
                    }
                }
                 */
                AppAlertDialog(titleText = "Error", messageText = message, confirmText = "OK")
            }

            is UiState.Success -> {
                LazyColumn {
                    items(items = uiState.items, key = { it.id }) {
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
            }
        }
    }
}
