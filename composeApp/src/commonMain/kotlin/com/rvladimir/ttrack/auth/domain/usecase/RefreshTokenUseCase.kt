package com.rvladimir.ttrack.auth.domain.usecase

import com.rvladimir.ttrack.auth.domain.model.AuthResult
import com.rvladimir.ttrack.auth.domain.repository.AuthRepository

/**
 * Use case that exchanges the stored refresh token for a fresh token pair.
 *
 * Handles token rotation: on success, the caller must persist the returned
 * [AuthResult.accessToken] and [AuthResult.refreshToken] immediately, since
 * the server invalidates the old refresh token on each call.
 *
 * @property repository The authentication repository.
 */
class RefreshTokenUseCase(
    private val repository: AuthRepository,
) {
    /**
     * Reads the stored refresh token and exchanges it for a new token pair.
     *
     * @return [Result] wrapping [AuthResult] on success, or a failure if no
     *   refresh token is stored or the server rejects it (e.g. expired/revoked).
     */
    suspend operator fun invoke(): Result<AuthResult> {
        val refreshToken =
            repository.getRefreshToken()
                ?: return Result.failure(IllegalStateException("No refresh token available. User must log in again."))
        return repository.refreshToken(refreshToken)
    }
}
