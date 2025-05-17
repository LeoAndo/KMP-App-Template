package com.jetbrains.kmpapp

import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSLog
import platform.Foundation.NSProcessInfo
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.UIKit.UIApplication

internal actual fun launchExternalBrowser(url: String) {
    val nsUrl = NSURL.URLWithString(url) ?: return
    UIApplication.sharedApplication.openURL(
        nsUrl,
        options = emptyMap<Any?, Any>(),
        completionHandler = null
    )
}

internal actual fun getUptimeMillis(): Long {
    // systemUptime は秒単位の Double だが、ミリ秒に変換して Long にキャストしても、現実的な稼働時間（数十年分）であれば精度や桁溢れの問題は発生しない
    return (NSProcessInfo.processInfo.systemUptime * 1000).toLong()
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual object SecretKeyProvider {
    actual val githubAccessToken =
        platform.Foundation.NSBundle.mainBundle.infoDictionary?.get("GITHUB_ACCESS_TOKEN") as String
}

internal actual fun logDebug(tag: String, message: String) {
    NSLog("[DEBUG] [$tag] $message")
}

internal actual fun logError(tag: String, message: String, throwable: Throwable?) {
    val fullMessage = if (throwable != null) {
        "$message: ${throwable.message ?: throwable::class.simpleName}"
    } else {
        message
    }
    NSLog("[ERROR] [$tag] $fullMessage")
}

@OptIn(ExperimentalForeignApi::class)
internal actual fun createSettingsDataStore(): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
        produceFile = {
        val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        val pathStr = requireNotNull(documentDirectory).path + "/$settingsDataStoreFileName"
        pathStr.toPath()
    })