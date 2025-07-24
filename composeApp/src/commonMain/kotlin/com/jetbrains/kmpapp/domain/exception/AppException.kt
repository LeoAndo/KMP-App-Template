package com.jetbrains.kmpapp.domain.exception

/**
 * アプリケーション固有の例外クラス
 */
internal sealed class AppException : Exception() {
    // 通信周りのエラー
    data class UnAuthorized(override val message: String) : AppException()
    data class Forbidden(override val message: String) : AppException()
    data class UnprocessableEntity(override val message: String) : AppException()
    data class Unexpected(override val message: String = "An unexpected error has occurred.") :
        AppException()

    data class NotFound(override val message: String) : AppException()
    data class Redirect(override val message: String) : AppException() // 300番台のエラー
    data class ServiceUnavailable(override val message: String = "Service unavailable. Please try again later.") :
        AppException() // 500番台のエラー

    data class Network(override val message: String = "Network error occurred. Please check your network connection.") :
        AppException() // ネットワークエラー
}