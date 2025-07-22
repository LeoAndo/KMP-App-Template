package com.jetbrains.kmpapp.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jetbrains.kmpapp.screens.theme.AppTheme

@Composable
internal fun SettingsScreen(onThemeChange: (AppTheme) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(WindowInsets.safeDrawing.asPaddingValues()).padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
    ) {
        Text(text = "Change App Theme")
        AppTheme.entries.forEach { appTheme ->
            Button({ onThemeChange(appTheme) }) { Text(text = appTheme.displayName) }
        }
    }
}
