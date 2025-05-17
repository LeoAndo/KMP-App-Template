package com.jetbrains.kmpapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetbrains.kmpapp.data.settings.SettingsDataStore
import com.jetbrains.kmpapp.screens.theme.AppTheme
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class AppViewModel(private val settingsDataStore: SettingsDataStore) : ViewModel() {

    var errorState by mutableStateOf<Throwable?>(null)
        private set

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        errorState = exception
    }

    val currentTheme: StateFlow<AppTheme> = settingsDataStore.selectedTheme.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AppTheme.TYPE01
    )

    fun changeTheme(theme: AppTheme) {
        viewModelScope.launch(coroutineExceptionHandler) {
            settingsDataStore.saveTheme(theme)
        }
    }

    fun clearError() {
        errorState = null
    }
}

