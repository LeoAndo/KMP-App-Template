package com.jetbrains.kmpapp.data.githubsearch

import com.jetbrains.kmpapp.data.KtorHandler
import com.jetbrains.kmpapp.domain.exception.AppException
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlin.coroutines.cancellation.CancellationException

class GithubApi(private val json: Json) {
    // APIごとに異なる設定を持つHttpClientを作成する
    private val httpClient: HttpClient by lazy {
        HttpClient {
            defaultRequest {
                url.takeFrom(URLBuilder().takeFrom(GITHUB_API_DOMAIN).apply {
                    encodedPath += url.encodedPath
                })
                header("Accept", "application/vnd.github.v3+json")
                header(
                    "Authorization",
                    "Bearer $GITHUB_ACCESS_TOKEN"
                )  // TODO Uncomment this line if you want to test without setting the token
                header("X-GitHub-Api-Version", "2022-11-28")
            }
            install(HttpTimeout) {
                requestTimeoutMillis = TIMEOUT_MILLIS
                connectTimeoutMillis = TIMEOUT_MILLIS
                socketTimeoutMillis = TIMEOUT_MILLIS
            }
            expectSuccess = true // HttpResponseValidatorで必要な設定.
            HttpResponseValidator {
                handleResponseExceptionWithRequest { e, _ ->
                    when (e) {
                        is ClientRequestException -> { // ktor: 400番台のエラー
                            val errorResponse = e.response
                            // APIの仕様に合わせて想定されるエラーのみ処理する
                            when (errorResponse.status) {
                                HttpStatusCode.Unauthorized -> { // "Bearer $GITHUB_ACCESS_TOKEN"が不正な場合
                                    val message =
                                        json.decodeFromString<GithubErrorResponse>(errorResponse.body()).message
                                    throw AppException.UnAuthorized(message)
                                }

                                HttpStatusCode.NotFound -> { // endpointが存在しない場合
                                    val message =
                                        json.decodeFromString<GithubErrorResponse>(errorResponse.body()).message
                                    throw AppException.NotFound(message)
                                }

                                HttpStatusCode.Forbidden -> { // requestのrate limitを超えた場合
                                    val message =
                                        json.decodeFromString<GithubErrorResponse>(errorResponse.body()).message
                                    throw AppException.Forbidden(message)
                                }

                                HttpStatusCode.UnprocessableEntity -> { // requestのパラメータが不正な場合
                                    val message =
                                        json.decodeFromString<GithubErrorResponse>(errorResponse.body()).message
                                    throw AppException.UnprocessableEntity(message)
                                }

                                // 他の400番台のエラーでもレスポンスBodyの形式がGithubErrorResponseと同じか不明のためエラーメッセージを設定する
                                else -> throw AppException.Unknown(e.message)
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
        const val SEARCH_PER_PAGE = 5
        private const val TIMEOUT_MILLIS: Long = 3 * 1000 // TODO Adjust this value for debugging
        private const val GITHUB_API_DOMAIN = "https://api.github.com/"
        private const val GITHUB_ACCESS_TOKEN =
            "YOUR GITHUB ACCESS TOKEN" // TODO Set the token to access the API.
    }
}
