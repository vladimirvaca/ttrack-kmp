package com.rvladimir.ttrack.auth.domain.usecase

import com.rvladimir.ttrack.auth.domain.repository.AuthRepository

/**
 * Use case that checks whether a valid session token is already stored.
 *
 * Returns the token string if the user is logged in, or `null` otherwise.
 *
 * @property repository The authentication repository.
 */
class GetSessionUseCase(
    private val repository: AuthRepository,
) {
    /**
     * @return The stored session token, or `null` if no session exists.
     */
    operator fun invoke(): String? = repository.getToken()
}
