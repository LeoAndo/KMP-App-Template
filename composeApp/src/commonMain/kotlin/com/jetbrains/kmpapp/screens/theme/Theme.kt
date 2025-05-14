package com.jetbrains.kmpapp.screens.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val lightColorScheme =
    lightColorScheme(primary = md_theme_light_primary, secondary = md_theme_light_secondary)

private val darkColorScheme =
    darkColorScheme(primary = md_theme_dark_primary, secondary = md_theme_dark_secondary)

@Composable
fun MyMaterialTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) darkColorScheme else lightColorScheme
    MaterialTheme(colorScheme = colors, content = content)
}