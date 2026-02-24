package com.rvladimir.ttrack.auth.data.repository

import com.rvladimir.ttrack.auth.data.remote.AuthApiService
import com.rvladimir.ttrack.auth.domain.model.AuthResult
import com.rvladimir.ttrack.auth.domain.repository.AuthRepository

/**
 * Concrete implementation of [AuthRepository] that delegates to [AuthApiService].
 *
 * @property apiService The remote API service.
 */
class AuthRepositoryImpl(
    private val apiService: AuthApiService,
) : AuthRepository {
    override suspend fun login(
        email: String,
        password: String,
    ): Result<AuthResult> =
        runCatching {
            val dto = apiService.login(email, password)
            AuthResult(token = dto.token, userId = dto.userId)
        }
}
