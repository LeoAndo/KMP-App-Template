package com.jetbrains.kmpapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jetbrains.kmpapp.screens.component.AppSurface
import com.jetbrains.kmpapp.screens.githubsearch.GithubSearchScreen
import com.jetbrains.kmpapp.screens.githubsearch.paging.GithubSearchPagingScreen
import com.jetbrains.kmpapp.screens.pokemon.PokemonPagingScreen
import com.jetbrains.kmpapp.screens.quiz.QuizScreen
import com.jetbrains.kmpapp.screens.settings.SettingsScreen
import com.jetbrains.kmpapp.screens.theme.MyMaterialTheme
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

/*
@Serializable
object ListDestination

@Serializable
data class DetailDestination(val objectId: Int)

@Composable
fun App() {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    ) {
        Surface {
            val navController: NavHostController = rememberNavController()
            NavHost(navController = navController, startDestination = ListDestination) {
                composable<ListDestination> {
                    ListScreen(navigateToDetails = { objectId ->
                        navController.navigate(DetailDestination(objectId))
                    })
                }
                composable<DetailDestination> { backStackEntry ->
                    DetailScreen(
                        objectId = backStackEntry.toRoute<DetailDestination>().objectId,
                        navigateBack = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}
 */


@Serializable
object MainScreen

@Serializable
object GithubSearchScreen

@Serializable
object GithubSearchPagingScreen

@Serializable
object QuizScreen

@Serializable
object SettingsScreen

@Serializable
object PokemonPagingScreen

// entry point.
@Composable
internal fun App() {
    // ThemeViewModelを取得
    val appViewModel = koinViewModel<AppViewModel>()
    val currentAppTheme by appViewModel.currentTheme.collectAsStateWithLifecycle()

    MyMaterialTheme(appTheme = currentAppTheme) {
        AppSurface {
            appViewModel.errorState?.let { errorState ->
                // エラーが発生している場合はダイアログを表示する
                AlertDialog(
                    onDismissRequest = { appViewModel.clearError() },
                    title = { Text("エラー") },
                    text = { Text("設定値の保存に失敗しました") },
                    confirmButton = { Button(onClick = { appViewModel.clearError() }) { Text("OK") } }
                )
            }

            val navController: NavHostController = rememberNavController()
            NavHost(navController = navController, startDestination = MainScreen) {
                composable<MainScreen> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Button({
                            navController.navigate(GithubSearchScreen)
                        }) {
                            Text("Ktor Test")
                        }
                        Button({
                            navController.navigate(GithubSearchPagingScreen)
                        }) {
                            Text("Ktor & Paging Test")
                        }
                        Button({
                            navController.navigate(QuizScreen)
                        }) {
                            Text("Quiz App")
                        }
                        Button({
                            navController.navigate(SettingsScreen)
                        }) {
                            Text("Theme Settings")
                        }
                        Button({
                            navController.navigate(PokemonPagingScreen)
                        }) {
                            Text("Pokemon API")
                        }
                    }
                }

                composable<GithubSearchScreen> { backStackEntry ->
                    GithubSearchScreen()
                }

                composable<GithubSearchPagingScreen> { backStackEntry ->
                    GithubSearchPagingScreen()
                }

                composable<QuizScreen> { backStackEntry ->
                    QuizScreen()
                }

                composable<SettingsScreen> { backStackEntry ->
                    SettingsScreen(onThemeChange = { appViewModel.changeTheme(it) })
                }

                composable<PokemonPagingScreen> { backStackEntry ->
                    PokemonPagingScreen()
                }
            }
        }
    }
}
