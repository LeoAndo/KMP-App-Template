package com.jetbrains.kmpapp.data

import com.jetbrains.kmpapp.domain.exception.AppException
import io.ktor.client.network.sockets.*
import io.ktor.client.plugins.*

/**
 * Ktor のエラーハンドリングを行うクラス.
 */
object KtorHandler {
    /**
     * 共通のAPI エラーハンドリングはここで行う.
     */
    @Throws(AppException::class)
    fun handleResponseException(e: Throwable) {
        when (e) {
            is HttpRequestTimeoutException, is ConnectTimeoutException, is SocketTimeoutException -> {
                throw AppException.Network(e.message ?: "Network error")
            }
            // ktor: 300番台のエラー
            is RedirectResponseException -> throw AppException.Redirect(e.message)
            // ktor: 400番台のエラー
            // is ClientRequestException -> throw AppException.XXXX(e.message)
            // ktor: 500番台のエラー
            is ServerResponseException -> throw AppException.Server(e.message)
            // ktor: それ以外のエラー
            is ResponseException -> throw AppException.Unknown(e.message ?: "Unknown error")
            else -> throw AppException.Unknown(e.message ?: "Unknown error")
        }
    }
}