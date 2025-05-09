package com.jetbrains.kmpapp

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual fun launchExternalBrowser(url: String) {
    val nsUrl = NSURL.URLWithString(url) ?: return
    UIApplication.sharedApplication.openURL(
        nsUrl,
        options = emptyMap<Any?, Any>(),
        completionHandler = null
    )
}