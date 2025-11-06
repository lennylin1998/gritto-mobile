package com.gritto.app.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

internal fun provideJson(): Json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    explicitNulls = false
}

internal fun createHttpClient(
    engineFactory: HttpClientEngineFactory<*>,
    json: Json = provideJson(),
): HttpClient = HttpClient(engineFactory) {
    install(ContentNegotiation) {
        json(json)
    }
    install(Logging) {
        level = LogLevel.INFO
        logger = object : Logger {
            override fun log(message: String) {
                println("GrittoHttpClient: $message")
            }
        }
    }
}

expect fun platformHttpClientEngine(): HttpClientEngineFactory<*>
