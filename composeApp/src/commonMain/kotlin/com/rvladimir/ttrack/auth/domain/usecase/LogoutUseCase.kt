package com.rvladimir.ttrack.auth.domain.usecase

import com.rvladimir.ttrack.auth.domain.repository.AuthRepository

/**
 * Use case that clears all persisted session tokens, effectively logging the user out.
 *
 * @property repository The authentication repository.
 */
class LogoutUseCase(
    private val repository: AuthRepository,
) {
    /** Clears the stored access token and refresh token. */
    operator fun invoke() = repository.clearTokens()
}
