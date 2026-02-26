package com.rvladimir.ttrack.auth.domain.usecase

import com.rvladimir.ttrack.auth.domain.repository.AuthRepository

/**
 * Use case that checks whether a valid session exists by inspecting the
 * stored access token.
 *
 * Returns the access token string if the user is logged in, or `null` otherwise.
 *
 * @property repository The authentication repository.
 */
class GetSessionUseCase(
    private val repository: AuthRepository,
) {
    /**
     * @return The stored access token, or `null` if no session exists.
     */
    operator fun invoke(): String? = repository.getAccessToken()
}
