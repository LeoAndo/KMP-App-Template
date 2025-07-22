package com.jetbrains.kmpapp.data.settings

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.jetbrains.kmpapp.createSettingsDataStore
import com.jetbrains.kmpapp.screens.theme.AppTheme
import kotlinx.coroutines.flow.map

internal class SettingsDataStore {

    private val dataStore by lazy { createSettingsDataStore() }

    private object PreferencesKeys {
        val APP_THEME = stringPreferencesKey("app_theme")
    }

    val selectedTheme = dataStore.data.map { preferences ->
        val themeName = preferences[PreferencesKeys.APP_THEME] ?: AppTheme.TYPE01.name
        AppTheme.valueOf(themeName)
    }

    suspend fun saveTheme(theme: AppTheme) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.APP_THEME] = theme.name
        }
    }
}