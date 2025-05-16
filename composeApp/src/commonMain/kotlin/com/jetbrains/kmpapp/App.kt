package com.jetbrains.kmpapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.jetbrains.kmpapp.screens.Screens
import com.jetbrains.kmpapp.screens.component.AppSurface
import com.jetbrains.kmpapp.screens.githubsearch.GithubSearchScreen
import com.jetbrains.kmpapp.screens.githubsearch.paging.GithubSearchPagingScreen
import com.jetbrains.kmpapp.screens.museum.MuseumApp
import com.jetbrains.kmpapp.screens.quiz.QuizScreen
import com.jetbrains.kmpapp.screens.theme.AppTheme
import com.jetbrains.kmpapp.screens.theme.MyMaterialTheme

// entry point.
@Composable
internal fun App() {
    var currentScreen by rememberSaveable { mutableStateOf(Screens.Init) }
    var currentAppTheme by rememberSaveable { mutableStateOf(AppTheme.TYPE01) }

    // Screensを使って表示する画面を切り替える
    MyMaterialTheme(appTheme = currentAppTheme) {
        when (currentScreen) {
            Screens.GithubSearch -> GithubSearchScreen(onBackClick = {
                currentScreen = Screens.Init
            })

            Screens.GithubSearchPaging -> GithubSearchPagingScreen()
            Screens.Museum -> MuseumApp()
            Screens.QUIZ -> QuizScreen(onBackClick = { currentScreen = Screens.Init })
            Screens.SETTINGS -> {
                AppSurface {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Change App Theme")
                        AppTheme.entries.forEach { appTheme ->
                            Button(
                                onClick = {
                                    currentAppTheme = appTheme
                                    currentScreen = Screens.Init
                                },
                            ) {
                                Text(text = appTheme.name)
                            }
                        }
                    }
                }
            }

            Screens.Init -> {
                AppSurface {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Screens.entries.filterNot { it == Screens.Init }.forEach { screen ->
                            Button(
                                onClick = { currentScreen = screen },
                            ) {
                                Text(text = screen.screenName)
                            }
                        }
                    }
                }
            }
        }
    }
}
