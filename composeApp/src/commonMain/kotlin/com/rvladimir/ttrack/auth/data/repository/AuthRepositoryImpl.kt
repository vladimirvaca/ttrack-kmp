package com.rvladimir.ttrack.auth.data.repository

import com.rvladimir.ttrack.auth.data.remote.AuthApiService
import com.rvladimir.ttrack.auth.domain.model.AuthResult
import com.rvladimir.ttrack.auth.domain.repository.AuthRepository
import com.rvladimir.ttrack.core.session.SessionStorage

/**
 * Concrete implementation of [AuthRepository] that delegates to [AuthApiService]
 * for network calls and [SessionStorage] for token persistence.
 *
 * @property apiService The remote API service.
 * @property sessionStorage Platform-specific storage for the session token.
 */
class AuthRepositoryImpl(
    private val apiService: AuthApiService,
    private val sessionStorage: SessionStorage,
) : AuthRepository {
    override suspend fun login(
        email: String,
        password: String,
    ): Result<AuthResult> =
        runCatching {
            val dto = apiService.login(email, password)
            AuthResult(
                accessToken =
                    dto.accessToken
                        ?: error("Login succeeded but no access token was returned."),
                tokenType = dto.tokenType,
            )
        }

    override fun saveToken(token: String) = sessionStorage.saveToken(token)

    override fun getToken(): String? = sessionStorage.getToken()

    override fun clearToken() = sessionStorage.clearToken()
}
