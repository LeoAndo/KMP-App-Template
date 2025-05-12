package com.jetbrains.kmpapp

import android.content.Intent
import android.os.SystemClock
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