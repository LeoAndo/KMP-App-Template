package com.jetbrains.kmpapp

import android.content.Intent
import android.os.SystemClock
import android.util.Log
import androidx.core.net.toUri

internal actual fun launchExternalBrowser(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    MyApp.instance.startActivity(intent)
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
    if (BuildConfig.DEBUG) Log.e(tag, message, throwable)
}