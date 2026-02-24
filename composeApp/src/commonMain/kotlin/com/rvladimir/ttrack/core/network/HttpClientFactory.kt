package com.rvladimir.ttrack.core.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Creates a shared, pre-configured [HttpClient].
 * The underlying engine is provided by each platform via [createPlatformHttpClient].
 */
fun createKtorClient(): HttpClient =
    createPlatformHttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    prettyPrint = false
                },
            )
        }
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.BODY
        }
    }

/** Platform-specific factory — implemented in androidMain and iosMain. */
expect fun createPlatformHttpClient(config: HttpClientConfig<*>.() -> Unit): HttpClient
