package com.jetbrains.kmpapp.data

import com.jetbrains.kmpapp.domain.exception.AppException
import com.jetbrains.kmpapp.logError
import io.ktor.client.network.sockets.*
import io.ktor.client.plugins.*
import io.ktor.http.HttpStatusCode

/**
 * Ktor のエラーハンドリングを行うクラス.
 */
internal object KtorHandler {
    /**
     * 共通のAPI エラーハンドリングはここで行う.
     */
    @Throws(AppException::class)
    fun handleResponseException(e: Throwable) {
        // jp) ここでエラーメッセージをstringResource(Res.string.xxx)などを使い多言語対応するのも良いかも
        val msg = e.message ?: "error message is not found"
        logError("KtorHandler", msg, e)
        when (e) {
            is HttpRequestTimeoutException, is ConnectTimeoutException, is SocketTimeoutException -> {
                throw AppException.Network("Network error occurred. Please check your network connection.")
            }
            // ktor: 300番台のエラー (通常スマホアプリでは使わない)
            is RedirectResponseException -> throw AppException.Redirect("${e.response.status}: ${e.message}")
            // ktor: 400番台のエラー
            // is ClientRequestException -> throw AppException.XXXX("${e.response.status}: ${e.message}")
            // ktor: 500番台のエラー
            is ServerResponseException -> {
                when(val status = e.response.status) {
                    HttpStatusCode.ServiceUnavailable -> throw AppException.ServiceUnavailable("Service unavailable. Please try again later.")
                    else -> throw AppException.Unexpected("An unexpected error has occurred.")
                }
            }
            // ktor: それ以外のエラー
            is ResponseException -> throw AppException.Unexpected("An unexpected error has occurred.")
            else -> throw AppException.Unexpected(msg)
        }
    }
}