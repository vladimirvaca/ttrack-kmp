package com.rvladimir.ttrack.auth.presentation

import com.rvladimir.ttrack.auth.data.remote.AuthApiService
import com.rvladimir.ttrack.auth.data.repository.AuthRepositoryImpl
import com.rvladimir.ttrack.auth.domain.usecase.GetSessionUseCase
import com.rvladimir.ttrack.auth.domain.usecase.LoginUseCase
import com.rvladimir.ttrack.core.network.createKtorClient
import com.rvladimir.ttrack.core.session.SessionStorage
import io.ktor.client.HttpClient

/**
 * Manual factory for [LoginViewModel] and shared auth use cases.
 *
 * Exposes [authenticatedClient], a Ktor [HttpClient] pre-configured with a
 * `Bearer` token provider backed by [SessionStorage]. Any feature that needs
 * to make authenticated API calls should use this client rather than creating
 * its own, so all requests automatically carry the current JWT.
 *
 * In production, prefer dependency injection (e.g. Koin or manual DI).
 */
object LoginViewModelFactory {
    private val sessionStorage = SessionStorage()

    private val repository by lazy {
        AuthRepositoryImpl(
            // Unauthenticated client — used only for the login call itself.
            apiService = AuthApiService(createKtorClient()),
            sessionStorage = sessionStorage,
        )
    }

    /**
     * A Ktor [HttpClient] that automatically attaches `Authorization: Bearer <token>`
     * to every request using the token stored in [SessionStorage].
     *
     * Use this client in all feature API services that require authentication.
     */
    val authenticatedClient: HttpClient by lazy {
        createKtorClient(tokenProvider = { sessionStorage.getToken() })
    }

    fun create(): LoginViewModel =
        LoginViewModel(
            loginUseCase = LoginUseCase(repository),
            repository = repository,
        )

    /** Returns a [GetSessionUseCase] backed by the same repository instance. */
    fun createGetSessionUseCase(): GetSessionUseCase = GetSessionUseCase(repository)
}
