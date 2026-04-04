package com.rvladimir.ttrack.auth.domain.usecase

import com.rvladimir.ttrack.auth.domain.model.UserSession
import com.rvladimir.ttrack.auth.domain.repository.AuthRepository

/**
 * Use case that encapsulates the login business logic.
 *
 * @property repository The authentication repository.
 */
class LoginUseCase(
    private val repository: AuthRepository,
) {
    /**
     * Executes the login operation with basic validation.
     *
     * On success the full session (tokens + user profile) is automatically persisted
     * via [AuthRepository.saveSession] so that the data survives app restarts.
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @return [Result] wrapping [UserSession] or a validation/network exception.
     */
    suspend operator fun invoke(
        email: String,
        password: String,
    ): Result<UserSession> {
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Email and password must not be empty."))
        }
        val result = repository.login(email.trim(), password)
        result.onSuccess { userSession ->
            repository.saveSession(userSession)
        }
        return result
    }
}
