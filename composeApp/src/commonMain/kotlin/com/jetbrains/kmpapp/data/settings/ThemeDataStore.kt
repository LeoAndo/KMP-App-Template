package com.jetbrains.kmpapp.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.jetbrains.kmpapp.domain.exception.AppExceptionHandler
import com.jetbrains.kmpapp.logError
import com.jetbrains.kmpapp.screens.theme.AppTheme
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * ThemeDataStore is a class for saving the app's theme settings
 *
 * @param dataStore The DataStore instance for storing preferences
 */
internal class ThemeDataStore(private val dataStore: DataStore<Preferences>) {

    private object PreferencesKeys {
        val APP_THEME = stringPreferencesKey("app_theme")
    }

    val selectedTheme = dataStore.data.catch { exception ->
        logError("ThemeDataStore", "Error reading preferences: ", exception)
        emit(emptyPreferences())
    }.map { preferences ->
        val themeName = preferences[PreferencesKeys.APP_THEME] ?: AppTheme.TYPE01.name
        AppTheme.valueOf(themeName)
    }

    suspend fun saveTheme(theme: AppTheme) {
        AppExceptionHandler.dataOrThrow {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.APP_THEME] = theme.name
            }
        }
    }
}