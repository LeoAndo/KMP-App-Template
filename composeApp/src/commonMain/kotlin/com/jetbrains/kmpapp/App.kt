package com.jetbrains.kmpapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jetbrains.kmpapp.domain.exception.AppException
import com.jetbrains.kmpapp.screens.Screens
import com.jetbrains.kmpapp.screens.component.AppSurface
import com.jetbrains.kmpapp.screens.githubsearch.GithubSearchScreen
import com.jetbrains.kmpapp.screens.githubsearch.paging.GithubSearchPagingScreen
import com.jetbrains.kmpapp.screens.museum.MuseumApp
import com.jetbrains.kmpapp.screens.quiz.QuizScreen
import com.jetbrains.kmpapp.screens.settings.SettingsScreen
import com.jetbrains.kmpapp.screens.theme.MyMaterialTheme
import org.koin.compose.viewmodel.koinViewModel

// entry point.
@Composable
internal fun App() {
    // 画面の切り替えはandroidx.navigation.composeパッケージを利用しないで簡易的な実装
    // Screensを使って表示する画面を切り替える
    var currentScreen by rememberSaveable { mutableStateOf(Screens.MAIN) }

    // ThemeViewModelを取得
    val appViewModel = koinViewModel<AppViewModel>()
    val currentAppTheme by appViewModel.currentTheme.collectAsState()

    MyMaterialTheme(appTheme = currentAppTheme) {
        appViewModel.errorState?.let { errorState ->
            // エラーが発生している場合はダイアログを表示する
            val errorMessage = when (errorState) {
                is AppException.DiskWrite -> "設定値の保存に失敗しました"
                else -> "予期せぬエラーが発生しました"
            }
            AlertDialog(
                onDismissRequest = { appViewModel.clearError() },
                title = { Text("エラー") },
                text = { Text(errorMessage) },
                confirmButton = { Button(onClick = { appViewModel.clearError() }) { Text("OK") } }
            )
        }

        when (currentScreen) {
            Screens.MUSEUM -> MuseumApp()
            Screens.GITHUB_SEARCH -> GithubSearchScreen(onBackClick = {
                currentScreen = Screens.MAIN
            })

            Screens.GITHUB_SEARCH_PAGING -> {
                GithubSearchPagingScreen(
                    modifier = Modifier.fillMaxSize()
                        .padding(WindowInsets.safeDrawing.asPaddingValues()).padding(12.dp)
                )
            }

            Screens.QUIZ -> QuizScreen(onBackClick = { currentScreen = Screens.MAIN })
            Screens.SETTINGS -> SettingsScreen(
                modifier = Modifier.fillMaxSize(),
                onBackClick = { currentScreen = Screens.MAIN },
                onThemeChange = { appViewModel.changeTheme(it) }
            )

            Screens.MAIN -> {
                AppSurface {
                    Column(
                        Modifier.wrapContentSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Screens.entries.filterNot { it == Screens.MAIN }.forEach { screen ->
                            Button({ currentScreen = screen }) {
                                Text(screen.screenName)
                            }
                        }
                    }
                }
            }
        }
    }
}