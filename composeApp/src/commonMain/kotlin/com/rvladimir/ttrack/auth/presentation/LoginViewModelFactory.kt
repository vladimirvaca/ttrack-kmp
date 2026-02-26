package com.rvladimir.ttrack.auth.presentation

import com.rvladimir.ttrack.auth.data.remote.AuthApiService
import com.rvladimir.ttrack.auth.data.repository.AuthRepositoryImpl
import com.rvladimir.ttrack.auth.domain.usecase.GetSessionUseCase
import com.rvladimir.ttrack.auth.domain.usecase.LoginUseCase
import com.rvladimir.ttrack.auth.domain.usecase.RefreshTokenUseCase
import com.rvladimir.ttrack.core.network.createKtorClient
import com.rvladimir.ttrack.core.session.SessionStorage
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.providers.BearerTokens

/**
 * Manual DI factory for auth-related objects.
 *
 * ### Authenticated Ktor client
 * [authenticatedClient] is a fully-wired [HttpClient] that:
 * 1. Attaches `Authorization: Bearer <accessToken>` to every request.
 * 2. On a **401** response, transparently calls `/auth/mobile-refresh`, rotates both
 *    tokens in [SessionStorage], and retries the original request — all without
 *    any extra code in the feature layer.
 * 3. If the refresh itself fails (expired/revoked refresh token), the request fails
 *    with a 401 and the caller is responsible for redirecting to the login screen.
 *
 * In production, replace this manual wiring with Koin or another DI framework.
 */
object LoginViewModelFactory {
    private val sessionStorage = SessionStorage()

    /** Unauthenticated client — only used for the login/refresh endpoint calls. */
    private val unauthenticatedClient by lazy { createKtorClient() }

    /** API service used exclusively for auth calls (login + refresh). */
    private val authApiService by lazy { AuthApiService(unauthenticatedClient) }

    private val repository by lazy {
        AuthRepositoryImpl(
            apiService = authApiService,
            sessionStorage = sessionStorage,
        )
    }

    /**
     * A Ktor [HttpClient] that automatically:
     * - Attaches `Authorization: Bearer <accessToken>` to every request.
     * - Silently refreshes the token pair on 401 and retries the original request.
     *
     * All feature API services that need authentication should share this single instance.
     */
    val authenticatedClient: HttpClient by lazy {
        createKtorClient(
            tokenProvider = {
                val access = sessionStorage.getAccessToken() ?: return@createKtorClient null
                val refresh = sessionStorage.getRefreshToken() ?: return@createKtorClient null
                BearerTokens(accessToken = access, refreshToken = refresh)
            },
            onRefreshTokens = {
                val currentRefresh =
                    sessionStorage.getRefreshToken()
                        ?: return@createKtorClient null
                val result =
                    runCatching { authApiService.refreshToken(currentRefresh) }
                        .getOrNull() ?: return@createKtorClient null
                // Persist rotated tokens immediately so all subsequent requests use them.
                sessionStorage.saveTokens(
                    accessToken = result.accessToken,
                    refreshToken = result.refreshToken,
                )
                BearerTokens(
                    accessToken = result.accessToken,
                    refreshToken = result.refreshToken,
                )
            },
        )
    }

    fun create(): LoginViewModel =
        LoginViewModel(
            loginUseCase = LoginUseCase(repository),
            repository = repository,
        )

    /** Returns a [GetSessionUseCase] backed by the shared repository instance. */
    fun createGetSessionUseCase(): GetSessionUseCase = GetSessionUseCase(repository)

    /** Returns a [RefreshTokenUseCase] backed by the shared repository instance. */
    fun createRefreshTokenUseCase(): RefreshTokenUseCase = RefreshTokenUseCase(repository)
}
