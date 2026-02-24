package com.rvladimir.ttrack.core.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Creates a shared, pre-configured [HttpClient].
 *
 * @param tokenProvider Optional lambda that returns the current JWT token.
 *   When provided, the `Authorization: Bearer <token>` header is automatically
 *   attached to every outgoing request via Ktor's [Auth] plugin.
 *   Pass `null` (the default) for unauthenticated clients such as the login call.
 */
fun createKtorClient(tokenProvider: (() -> String?)? = null): HttpClient =
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
        if (tokenProvider != null) {
            install(Auth) {
                bearer {
                    loadTokens {
                        val token = tokenProvider() ?: return@loadTokens null
                        BearerTokens(accessToken = token, refreshToken = "")
                    }
                    // No automatic refresh — token management is handled at the app level.
                    refreshTokens { null }
                }
            }
        }
    }

/** Platform-specific factory — implemented in androidMain and iosMain. */
expect fun createPlatformHttpClient(config: HttpClientConfig<*>.() -> Unit): HttpClient
