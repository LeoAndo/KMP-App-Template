package com.jetbrains.kmpapp.domain.exception

import com.jetbrains.kmpapp.logError
import kotlin.coroutines.cancellation.CancellationException

/**
 * アプリケーション固有の共通エラーハンドリングはここで行う.
 */
internal object AppExceptionHandler {
    @Throws(AppException::class, CancellationException::class)
    suspend fun <T> dataOrThrow(apiCall: suspend () -> T): T {
        return try {
            apiCall.invoke()
        } catch (e: Throwable) {
            val msg = e.message ?: "Unknown error"
            when (e) {
                is androidx.datastore.core.IOException -> throw throw AppException.DiskWrite(msg)
                else -> {
                    logError("AppExceptionHandler", msg, e) // 予期しないエラーのみログ出力
                    throw AppException.Unexpected(msg)
                }
            }
        }
    }
}