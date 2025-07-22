package com.jetbrains.kmpapp

import android.content.Intent
import android.os.SystemClock
import android.util.Log
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import okio.Path.Companion.toPath


internal actual fun launchExternalBrowser(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    MuseumApp.instance.startActivity(intent)
}

internal actual fun getUptimeMillis(): Long {
    return SystemClock.uptimeMillis()
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual object SecretKeyProvider {
    actual val githubAccessToken = BuildConfig.GITHUB_ACCESS_TOKEN
}

internal actual fun logDebug(tag: String, message: String) {
    if (BuildConfig.DEBUG) Log.d(tag, message)
}

internal actual fun logError(tag: String, message: String, throwable: Throwable?) {
    if (BuildConfig.DEBUG) {
        val fullMessage = if (throwable != null) {
            "$message: ${throwable.message ?: throwable::class.simpleName}"
        } else {
            message
        }
        Log.e(tag, fullMessage)
    }
}

internal actual fun createSettingsDataStore(): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
        produceFile = {
            val pathStr =
                MuseumApp.instance.applicationContext.filesDir.resolve(settingsDataStoreFileName).absolutePath
            pathStr.toPath()
        })