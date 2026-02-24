package com.rvladimir.ttrack.auth.presentation

import com.rvladimir.ttrack.auth.data.remote.AuthApiService
import com.rvladimir.ttrack.auth.data.repository.AuthRepositoryImpl
import com.rvladimir.ttrack.auth.domain.usecase.LoginUseCase
import com.rvladimir.ttrack.core.network.createKtorClient

/**
 * Manual factory for [LoginViewModel].
 *
 * Base URL and endpoint paths are configured centrally in [com.rvladimir.ttrack.core.network.AppConfig]
 * and [com.rvladimir.ttrack.auth.data.remote.AuthEndpoints].
 * In production, prefer dependency injection (e.g. Koin or manual DI).
 */
object LoginViewModelFactory {
    fun create(): LoginViewModel {
        val httpClient = createKtorClient()
        val apiService = AuthApiService(httpClient)
        val repository = AuthRepositoryImpl(apiService)
        val useCase = LoginUseCase(repository)
        return LoginViewModel(useCase)
    }
}
