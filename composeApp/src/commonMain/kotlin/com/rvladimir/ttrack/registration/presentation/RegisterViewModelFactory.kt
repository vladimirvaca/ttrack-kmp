package com.rvladimir.ttrack.registration.presentation

import com.rvladimir.ttrack.core.network.createKtorClient
import com.rvladimir.ttrack.registration.data.remote.UserApiService
import com.rvladimir.ttrack.registration.data.repository.RegisterRepositoryImpl
import com.rvladimir.ttrack.registration.domain.usecase.RegisterUseCase

/**
 * Manual DI factory for registration-related objects.
 *
 * Uses an unauthenticated Ktor client — registration does not require a JWT token.
 */
object RegisterViewModelFactory {
    /** Unauthenticated client — `POST /user/create` is a public endpoint. */
    private val httpClient by lazy { createKtorClient() }

    private val apiService by lazy { UserApiService(httpClient) }

    private val repository by lazy { RegisterRepositoryImpl(apiService) }

    /** Creates a [RegisterViewModel] with all dependencies wired. */
    fun create(): RegisterViewModel =
        RegisterViewModel(
            registerUseCase = RegisterUseCase(repository),
        )
}
