package com.rvladimir.ttrack.auth.presentation

import com.rvladimir.ttrack.auth.data.remote.AuthApiService
import com.rvladimir.ttrack.auth.data.repository.AuthRepositoryImpl
import com.rvladimir.ttrack.auth.domain.usecase.LoginUseCase
import com.rvladimir.ttrack.core.network.createKtorClient

/**
 * Manual factory for [LoginViewModel].
 *
 * Replace [BASE_URL] with your actual backend base URL.
 * In production, prefer dependency injection (e.g. Koin or manual DI).
 */
object LoginViewModelFactory {
    private const val BASE_URL = "https://api.ttrack.com" // TODO: replace with your backend URL

    fun create(): LoginViewModel {
        val httpClient = createKtorClient() // createKtorClient uses createPlatformHttpClient internally
        val apiService = AuthApiService(httpClient, BASE_URL)
        val repository = AuthRepositoryImpl(apiService)
        val useCase = LoginUseCase(repository)
        return LoginViewModel(useCase)
    }
}
