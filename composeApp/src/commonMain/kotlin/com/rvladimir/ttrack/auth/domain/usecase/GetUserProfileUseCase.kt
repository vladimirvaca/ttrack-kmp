package com.rvladimir.ttrack.auth.domain.usecase

import com.rvladimir.ttrack.auth.domain.model.UserSession
import com.rvladimir.ttrack.auth.domain.repository.AuthRepository

/**
 * Use case that reconstructs the stored [UserSession] from persisted session data.
 *
 * Returns `null` if any required field (token, user ID, name, email) is absent,
 * which indicates the user has not logged in yet or the session has been cleared.
 *
 * @property repository The authentication repository.
 */
class GetUserProfileUseCase(
    private val repository: AuthRepository,
) {
    /**
     * @return The full [UserSession] if a complete session is persisted, `null` otherwise.
     */
    operator fun invoke(): UserSession? {
        val accessToken = repository.getAccessToken() ?: return null
        val refreshToken = repository.getRefreshToken() ?: return null
        val userId = repository.getUserId() ?: return null
        val name = repository.getUserName() ?: return null
        val lastName = repository.getUserLastName() ?: return null
        val email = repository.getUserEmail() ?: return null
        return UserSession(
            accessToken = accessToken,
            refreshToken = refreshToken,
            userId = userId,
            name = name,
            lastName = lastName,
            email = email,
        )
    }
}
