package com.jetbrains.kmpapp.screens.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
internal fun MyMaterialTheme(appTheme: AppTheme, content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) appTheme.darkColorScheme else appTheme.lightColorScheme
    MaterialTheme(colorScheme = colors, content = content)
}