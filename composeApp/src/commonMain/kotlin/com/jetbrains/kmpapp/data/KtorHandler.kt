package com.jetbrains.kmpapp.data

import com.jetbrains.kmpapp.domain.exception.AppException
import com.jetbrains.kmpapp.logError
import io.ktor.client.network.sockets.*
import io.ktor.client.plugins.*

/**
 * Ktor のエラーハンドリングを行うクラス.
 */
internal object KtorHandler {
    /**
     * 共通のAPI エラーハンドリングはここで行う.
     */
    @Throws(AppException::class)
    fun handleResponseException(e: Throwable) {
        val msg = e.message ?: "Unknown error"
        logError("KtorHandler", msg, e)
        when (e) {
            is HttpRequestTimeoutException, is ConnectTimeoutException, is SocketTimeoutException -> {
                throw AppException.Network("Network error occurred. Please check your network connection.")
            }
            // ktor: 300番台のエラー
            is RedirectResponseException -> throw AppException.Redirect("${e.response.status}: ${e.message}")
            // ktor: 400番台のエラー
            // is ClientRequestException -> throw AppException.XXXX("${e.response.status}: ${e.message}")
            // ktor: 500番台のエラー
            is ServerResponseException -> throw AppException.Server("${e.response.status}: ${e.message}")
            // ktor: それ以外のエラー
            is ResponseException -> throw AppException.Unknown("${e.response.status}: ${e.message}")
            else -> throw AppException.Unknown(msg)
        }
    }
}