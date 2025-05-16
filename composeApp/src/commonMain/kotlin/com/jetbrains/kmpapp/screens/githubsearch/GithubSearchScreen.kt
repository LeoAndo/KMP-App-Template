package com.jetbrains.kmpapp.screens.githubsearch

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jetbrains.kmpapp.domain.exception.AppException
import com.jetbrains.kmpapp.launchExternalBrowser
import com.jetbrains.kmpapp.screens.component.AppError
import com.jetbrains.kmpapp.screens.component.EmptyScreenContent
import com.jetbrains.kmpapp.screens.component.AppLoading
import kmp_app_template.composeapp.generated.resources.Res
import kmp_app_template.composeapp.generated.resources.back
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun GithubSearchScreen(modifier: Modifier = Modifier, onBackClick: () -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    var sortType by rememberSaveable { mutableStateOf(SortType.STARS) }
    var expanded by rememberSaveable { mutableStateOf(false) }
    val viewModel = koinViewModel<GithubSearchViewModel>()
    SearchScreenStateless(
        modifier = modifier,
        query = query,
        sortType = sortType,
        uiState = viewModel.uiState,
        expanded = expanded,
        onValueChange = { query = it },
        onSortTypeChange = { sortType = it },
        onSearch = { viewModel.searchRepositories(query, 1, sortType.sort) },
        onClickItem = { url -> launchExternalBrowser(url) },
        onDropdownMenuExpanded = { expanded = it },
        onBackClick = { onBackClick() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
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
    onBackClick: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = "Github Search") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(Res.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(12.dp)) {
            OutlinedTextField(
                value = query,
                label = {
                    Text(text = "Search GitHub Repositories")
                },
                singleLine = true,
                maxLines = 1,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                    onSearch()
                }),
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

            when (uiState) {
                UiState.Initial -> EmptyScreenContent(Modifier.fillMaxSize())
                UiState.Loading -> AppLoading(modifier = Modifier.fillMaxSize())
                is UiState.Failure -> {
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
                    AppError(
                        message = message,
                        onReload = onSearch,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is UiState.Success -> {
                    LazyColumn {
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
                }
            }
        }
    }
}
