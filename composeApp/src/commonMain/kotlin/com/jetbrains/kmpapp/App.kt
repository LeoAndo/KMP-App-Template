package com.jetbrains.kmpapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jetbrains.kmpapp.screens.Screens
import com.jetbrains.kmpapp.screens.component.AppSurface
import com.jetbrains.kmpapp.screens.githubsearch.GithubSearchScreen
import com.jetbrains.kmpapp.screens.githubsearch.paging.GithubSearchPagingScreen
import com.jetbrains.kmpapp.screens.museum.MuseumApp

// entry point.
@Composable
internal fun App() {
    var screens by remember { mutableStateOf(Screens.Init) }
    // Screensを使って表示する画面を切り替える
    when (screens) {
        Screens.GithubSearch -> GithubSearchScreen()
        Screens.GithubSearchPaging -> GithubSearchPagingScreen()
        Screens.Museum -> MuseumApp()
        Screens.Init -> {
            AppSurface {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Screens.entries.filterNot { it == Screens.Init }.forEach { screen ->
                        Button(
                            onClick = { screens = screen },
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(text = screen.screenName)
                        }
                    }
                }
            }
        }
    }
}
