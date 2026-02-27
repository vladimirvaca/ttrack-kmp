package com.rvladimir.ttrack.registration.domain.usecase

import com.rvladimir.ttrack.registration.domain.repository.RegisterRepository

/**
 * Use case that encapsulates the user-registration business logic.
 *
 * Validates all required fields before delegating to [RegisterRepository].
 *
 * @property repository The registration repository.
 */
class RegisterUseCase(
    private val repository: RegisterRepository,
) {
    /**
     * Executes the registration operation with basic validation.
     *
     * @param firstName The user's first name (required, non-blank).
     * @param lastName The user's last name (required, non-blank).
     * @param nickname The user's display nickname (required, non-blank).
     * @param dateBirth The user's date of birth in ISO-8601 format `"YYYY-MM-DD"` (required, non-blank).
     * @param email The user's email address (required, non-blank).
     * @param password The user's chosen password (required, non-blank).
     * @return [Result.success] with [Unit] on success, or [Result.failure] with a validation/network exception.
     */
    suspend operator fun invoke(
        firstName: String,
        lastName: String,
        nickname: String,
        dateBirth: String,
        email: String,
        password: String,
    ): Result<Unit> {
        if (firstName.isBlank() ||
            lastName.isBlank() ||
            nickname.isBlank() ||
            dateBirth.isBlank() ||
            email.isBlank() ||
            password.isBlank()
        ) {
            return Result.failure(IllegalArgumentException("Please fill in all required fields."))
        }
        return repository.register(
            firstName = firstName.trim(),
            lastName = lastName.trim(),
            nickname = nickname.trim(),
            dateBirth = dateBirth.trim(),
            email = email.trim(),
            password = password,
        )
    }
}
