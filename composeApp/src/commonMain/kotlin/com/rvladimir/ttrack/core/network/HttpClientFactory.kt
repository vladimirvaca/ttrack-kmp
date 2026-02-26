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
 * ### Unauthenticated client
 * Pass `null` for [tokenProvider] (the default). Used for the login endpoint itself.
 *
 * ### Authenticated client with automatic token refresh
 * Supply both [tokenProvider] and [onRefreshTokens].
 * - [tokenProvider] returns the current `accessToken` / `refreshToken` pair for outgoing requests.
 * - [onRefreshTokens] is invoked by Ktor automatically when a **401** response is received.
 *   It should call the `/auth/mobile-refresh` endpoint and return fresh [BearerTokens],
 *   or `null` if the refresh itself fails (forcing the user to log in again).
 *
 * Token rotation is handled transparently: Ktor retries the original request with the new
 * access token after a successful refresh, with no extra wiring required in the call sites.
 *
 * @param tokenProvider Lambda that returns the current [BearerTokens], or `null` when unauthenticated.
 * @param onRefreshTokens Suspend lambda invoked on 401 to obtain fresh tokens; return `null` to signal
 *   that the session is unrecoverable (user must log in again).
 */
fun createKtorClient(
    tokenProvider: (suspend () -> BearerTokens?)? = null,
    onRefreshTokens: (suspend () -> BearerTokens?)? = null,
): HttpClient =
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
                    loadTokens { tokenProvider() }
                    refreshTokens {
                        onRefreshTokens?.invoke()
                    }
                }
            }
        }
    }

/** Platform-specific factory — implemented in androidMain and iosMain. */
expect fun createPlatformHttpClient(config: HttpClientConfig<*>.() -> Unit): HttpClient
