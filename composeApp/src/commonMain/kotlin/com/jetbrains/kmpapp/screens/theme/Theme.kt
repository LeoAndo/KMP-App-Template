package com.jetbrains.kmpapp.screens.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
internal fun MyMaterialTheme(
    appThemeType: AppThemeType = AppThemeType.TYPE01,
    content: @Composable () -> Unit
) {
    val colors =
        if (isSystemInDarkTheme()) appThemeType.darkColorScheme else appThemeType.lightColorScheme
    MaterialTheme(colorScheme = colors, content = content)
}