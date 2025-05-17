package com.jetbrains.kmpapp

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

// 各Platform APIの実装用のインターフェースをここにまとめる
internal expect fun launchExternalBrowser(url: String)

// 端末の起動からの経過時間(ミリ秒)を取得する
internal expect fun getUptimeMillis(): Long

// 秘匿情報の管理用object (実験段階のAPIのため、ワーニングが現状発生する)
// https://github.com/yshrsmz/BuildKonfig　は使わない
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal expect object SecretKeyProvider {
    val githubAccessToken: String
}

// デバッグ用のログ出力関数
internal expect fun logDebug(tag: String, message: String)
internal expect fun logError(tag: String, message: String, throwable: Throwable? = null)

// DataStoreのインスタンスを提供するexpect宣言
internal expect fun createSettingsDataStore(): DataStore<Preferences>
internal const val settingsDataStoreFileName = "settings.preferences_pb"