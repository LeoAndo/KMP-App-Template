package com.jetbrains.kmpapp.data.pokemon

import com.jetbrains.kmpapp.data.KtorHandler
import com.jetbrains.kmpapp.domain.exception.AppException
import com.jetbrains.kmpapp.logError
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlin.coroutines.cancellation.CancellationException

internal class PokemonApi(private val json: Json) {
    // APIごとに異なる設定を持つHttpClientを作成する
    private val httpClient: HttpClient by lazy {
        HttpClient {
            defaultRequest {
                url.takeFrom(URLBuilder().takeFrom(API_DOMAIN).apply {
                    encodedPath += url.encodedPath
                })
            }
            install(HttpTimeout) {
                requestTimeoutMillis = TIMEOUT_MILLIS
                connectTimeoutMillis = TIMEOUT_MILLIS
                socketTimeoutMillis = TIMEOUT_MILLIS
            }
            expectSuccess = true // HttpResponseValidatorで必要な設定.
            HttpResponseValidator {
                handleResponseExceptionWithRequest { e, _ ->
                    // jp) Web API固有のエラーハンドリングをここで行う
                    when (e) {
                        is ClientRequestException -> { // ktor: 400番台のエラー
                            val errorResponse = e.response
                            logError("PokemonApi", errorResponse.toString())

                            // APIの仕様に合わせて想定されるエラーのみ処理する
                            when (val status = errorResponse.status) {
                                HttpStatusCode.NotFound -> { // endpointが存在しない場合
                                    throw AppException.NotFound("Pokemonが見つかりませんでした")
                                }

                                else -> throw AppException.Unexpected("${status}: ${e.message}")
                            }
                        }

                        else -> KtorHandler.handleResponseException(e)
                    }
                }
            }
        }
    }

    @Throws(AppException::class, CancellationException::class)
    suspend fun fetchPokemon(
        offset: Int, // 取得開始位置 (0から始まる)
        limit: Int = LIMIT, // 1ページに取得する件数
    ): PokemonResponse {
        val response = httpClient.get {
            url { path("pokemon") }
            parameter("offset", offset)
            parameter("limit", limit)
        }
        return json.decodeFromString(response.body())
    }

    companion object {
        const val LIMIT = 20 // 1ページに取得する件数
        private const val TIMEOUT_MILLIS: Long = 20 * 1000 // TODO Adjust this value for debugging
        private const val API_DOMAIN = "https://pokeapi.co/api/v2/"
    }
}
