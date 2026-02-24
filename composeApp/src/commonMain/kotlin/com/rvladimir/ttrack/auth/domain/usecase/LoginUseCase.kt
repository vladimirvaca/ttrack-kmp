package com.rvladimir.ttrack.auth.domain.usecase

import com.rvladimir.ttrack.auth.domain.model.AuthResult
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
     * @param email The user's email address.
     * @param password The user's password.
     * @return [Result] wrapping [AuthResult] or a validation/network exception.
     */
    suspend operator fun invoke(
        email: String,
        password: String,
    ): Result<AuthResult> {
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Email and password must not be empty."))
        }
        return repository.login(email.trim(), password)
    }
}
