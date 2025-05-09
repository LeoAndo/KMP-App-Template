package com.jetbrains.kmpapp.domain.exception

/**
 * アプリケーション固有の例外クラス
 */
sealed class AppException : Exception() {
    data class UnAuthorized(override val message: String) : AppException()
    data class Forbidden(override val message: String) : AppException()
    data class UnprocessableEntity(override val message: String) : AppException()
    data class Unknown(override val message: String) : AppException()
    data class NotFound(override val message: String) : AppException()
    data class Redirect(override val message: String) : AppException() // 300番台のエラー
    data class Server(override val message: String) : AppException() // 500番台のエラー
    data class Network(override val message: String) : AppException() // ネットワークエラー
}