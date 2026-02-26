package com.rvladimir.ttrack.auth.data.repository

import com.rvladimir.ttrack.auth.data.remote.AuthApiService
import com.rvladimir.ttrack.auth.domain.model.AuthResult
import com.rvladimir.ttrack.auth.domain.repository.AuthRepository
import com.rvladimir.ttrack.core.session.SessionStorage
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode

/**
 * Concrete implementation of [AuthRepository] that delegates to [AuthApiService]
 * for network calls and [SessionStorage] for token persistence.
 *
 * @property apiService The remote API service.
 * @property sessionStorage Platform-specific storage for session tokens.
 */
class AuthRepositoryImpl(
    private val apiService: AuthApiService,
    private val sessionStorage: SessionStorage,
) : AuthRepository {
    override suspend fun login(
        email: String,
        password: String,
    ): Result<AuthResult> =
        try {
            val dto = apiService.login(email, password)
            Result.success(
                AuthResult(
                    accessToken = dto.accessToken,
                    refreshToken = dto.refreshToken,
                    tokenType = dto.tokenType,
                ),
            )
        } catch (e: ClientRequestException) {
            if (e.response.status == HttpStatusCode.Unauthorized) {
                Result.failure(Exception("Invalid mail or password."))
            } else {
                Result.failure(e)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun refreshToken(refreshToken: String): Result<AuthResult> =
        runCatching {
            val dto = apiService.refreshToken(refreshToken)
            AuthResult(
                accessToken = dto.accessToken,
                refreshToken = dto.refreshToken,
                tokenType = dto.tokenType,
            )
        }

    override fun saveTokens(
        accessToken: String,
        refreshToken: String,
    ) = sessionStorage.saveTokens(accessToken, refreshToken)

    override fun getAccessToken(): String? = sessionStorage.getAccessToken()

    override fun getRefreshToken(): String? = sessionStorage.getRefreshToken()

    override fun clearTokens() = sessionStorage.clearTokens()
}
