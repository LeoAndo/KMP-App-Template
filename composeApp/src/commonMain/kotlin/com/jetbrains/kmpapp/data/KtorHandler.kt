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
                throw AppException.Network()
            }
            // ktor: 300番台のエラー (通常スマホアプリでは使わない)
            is RedirectResponseException -> throw AppException.Redirect("${e.response.status}: ${e.message}")
            // ktor: 400番台のエラー
            // is ClientRequestException -> throw AppException.XXXX("${e.response.status}: ${e.message}")
            // ktor: 500番台のエラー
            is ServerResponseException -> {
                when(e.response.status) {
                    HttpStatusCode.ServiceUnavailable -> throw AppException.ServiceUnavailable()
                    else -> throw AppException.Unexpected()
                }
            }
            // ktor: それ以外のエラー
            is ResponseException -> throw AppException.Unexpected()
            else -> throw AppException.Unexpected(msg)
        }
    }
}