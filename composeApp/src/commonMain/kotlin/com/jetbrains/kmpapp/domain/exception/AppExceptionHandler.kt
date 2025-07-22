package com.jetbrains.kmpapp.domain.exception

import com.jetbrains.kmpapp.logError
import kotlin.coroutines.cancellation.CancellationException

/**
 * アプリケーション固有の共通エラーハンドリングはここで行う.
 */
internal object AppExceptionHandler {
    /*
    @Throws(AppException::class, CancellationException::class)
    suspend fun <T> dataOrThrow(apiCall: suspend () -> T): T {
        return try {
            apiCall.invoke()
        } catch (e: Throwable) {
            val msg = e.message ?: "Unknown error"
            logError("AppExceptionHandler", msg, e)
            when (e) {
                is androidx.datastore.core.IOException -> throw throw AppException.XXXXX(msg)
                else -> {
                    throw AppException.Unexpected(msg)
                }
            }
        }
    }
     */
}