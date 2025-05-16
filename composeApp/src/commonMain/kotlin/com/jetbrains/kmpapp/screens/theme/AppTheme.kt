package com.jetbrains.kmpapp.screens.theme

import androidx.compose.material3.ColorScheme

internal enum class AppTheme(
    val displayName: String,
    val lightColorScheme: ColorScheme,
    val darkColorScheme: ColorScheme
) {
    TYPE01(
        "Theme 01",
        com.jetbrains.kmpapp.screens.theme.type01.lightScheme,
        com.jetbrains.kmpapp.screens.theme.type01.darkScheme
    ),
    TYPE02(
        "Theme 02",
        com.jetbrains.kmpapp.screens.theme.type02.lightScheme,
        com.jetbrains.kmpapp.screens.theme.type02.darkScheme
    ),
    TYPE03(
        "Theme 03",
        com.jetbrains.kmpapp.screens.theme.type03.lightScheme,
        com.jetbrains.kmpapp.screens.theme.type03.darkScheme
    ),
}