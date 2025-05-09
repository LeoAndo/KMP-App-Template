package com.jetbrains.kmpapp

import android.content.Intent
import androidx.core.net.toUri

actual fun launchExternalBrowser(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    MyApp.instance.startActivity(intent)
}