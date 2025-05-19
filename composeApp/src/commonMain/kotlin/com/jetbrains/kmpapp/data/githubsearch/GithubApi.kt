package com.jetbrains.kmpapp.data.githubsearch

import com.jetbrains.kmpapp.SecretKeyProvider
import com.jetbrains.kmpapp.data.KtorHandler
import com.jetbrains.kmpapp.domain.exception.AppException
import com.jetbrains.kmpapp.logError
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlin.coroutines.cancellation.CancellationException

internal class GithubApi(private val json: Json) {
    // APIごとに異なる設定を持つHttpClientを作成する
    private val httpClient: HttpClient by lazy {
        HttpClient {
            defaultRequest {
                url.takeFrom(URLBuilder().takeFrom(GITHUB_API_DOMAIN).apply {
                    encodedPath += url.encodedPath
                })
                header("Accept", "application/vnd.github.v3+json")
                if (SecretKeyProvider.githubAccessToken.isNotEmpty()) {
                    header("Authorization", "Bearer ${SecretKeyProvider.githubAccessToken}")
                }
                header("X-GitHub-Api-Version", "2022-11-28")
                // header("Accept-Language", "ja-JP")
            }
            install(HttpTimeout) {
                requestTimeoutMillis = TIMEOUT_MILLIS
                connectTimeoutMillis = TIMEOUT_MILLIS
                socketTimeoutMillis = TIMEOUT_MILLIS
            }
            expectSuccess = true // HttpResponseValidatorで必要な設定.
            HttpResponseValidator {
                handleResponseExceptionWithRequest { e, _ ->
                    // jp) Web API固有のエラーハンドリングなので、ここでエラーメッセージをstringResource(Res.string.xxx)などを使い多言語対応するのも良いかも
                    when (e) {
                        is ClientRequestException -> { // ktor: 400番台のエラー
                            val errorResponse = e.response
                            logError("GithubApi", e.message)

                            // APIの仕様に合わせて想定されるエラーのみ処理する
                            when (val status = errorResponse.status) {
                                HttpStatusCode.Unauthorized -> { // "Bearer $GITHUB_ACCESS_TOKEN"が不正な場合
                                    val message =
                                        json.decodeFromString<GithubErrorResponse>(errorResponse.body()).message
                                    throw AppException.UnAuthorized("${status}: $message")
                                }

                                HttpStatusCode.NotFound -> { // endpointが存在しない場合
                                    val message =
                                        json.decodeFromString<GithubErrorResponse>(errorResponse.body()).message
                                    throw AppException.NotFound("${status}: $message")
                                }

                                HttpStatusCode.Forbidden -> { // requestのrate limitを超えた場合
                                    val message =
                                        json.decodeFromString<GithubErrorResponse>(errorResponse.body()).message
                                    throw AppException.Forbidden("${status}: $message")
                                }

                                HttpStatusCode.UnprocessableEntity -> { // requestのパラメータが不正な場合
                                    val message =
                                        json.decodeFromString<GithubErrorResponse>(errorResponse.body()).message
                                    throw AppException.UnprocessableEntity("${status}: $message")
                                }

                                // TODO 他の400番台のエラーでもレスポンスBodyの形式がGithubErrorResponseと同じか不明のためエラーメッセージを設定する
                                else -> throw AppException.Unexpected("An unexpected error has occurred.")
                            }
                        }

                        else -> KtorHandler.handleResponseException(e)
                    }
                }
            }
        }
    }

    @Throws(AppException::class, CancellationException::class)
    suspend fun searchRepositories(
        query: String,
        page: Int,
        sort: String,
        perPage: Int = SEARCH_PER_PAGE,
    ): SearchRepositoryResponse {
        val response: HttpResponse = httpClient.get {
            url { path("search", "repositories") }
            parameter("q", query)
            parameter("page", page)
            parameter("per_page", perPage)
            parameter("sort", sort)
        }
        return json.decodeFromString(response.body())
    }

    companion object {
        const val SEARCH_PER_PAGE = 50
        private const val TIMEOUT_MILLIS: Long = 3 * 1000 // TODO Adjust this value for debugging
        private const val GITHUB_API_DOMAIN = "https://api.github.com/"
    }
}
