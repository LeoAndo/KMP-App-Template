package com.jetbrains.kmpapp.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.CancellationException
import kotlinx.serialization.json.Json

interface MuseumApi {
    suspend fun getData(): List<MuseumObject>
}

class KtorMuseumApi() : MuseumApi {
    private val client: HttpClient by lazy {
        HttpClient {
            install(ContentNegotiation) {
                // TODO Fix API so it serves application/json
                json(Json { ignoreUnknownKeys = true }, contentType = ContentType.Any)
            }
        }
    }

    companion object {
        private const val API_URL =
            "https://raw.githubusercontent.com/Kotlin/KMP-App-Template/main/list.json"
    }

    override suspend fun getData(): List<MuseumObject> {
        return try {
            client.get(API_URL).body()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()

            emptyList()
        }
    }
}
