package com.jetbrains.kmpapp.screens.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jetbrains.kmpapp.screens.theme.MyMaterialTheme

@Composable
internal fun AppSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    MyMaterialTheme {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
            content = content,
        )
    }
}
