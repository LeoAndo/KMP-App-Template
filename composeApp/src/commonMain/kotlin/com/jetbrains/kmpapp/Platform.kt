package com.jetbrains.kmpapp

// 各Platform APIの実装用のインターフェースをここにまとめる
internal expect fun launchExternalBrowser(url: String)

// 端末の起動からの経過時間(ミリ秒)を取得する
internal expect fun getUptimeMillis(): Long