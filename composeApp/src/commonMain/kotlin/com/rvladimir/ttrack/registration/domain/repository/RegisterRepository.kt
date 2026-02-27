package com.rvladimir.ttrack.registration.domain.repository

/**
 * Contract for user-registration operations.
 * Implementations live in the data layer.
 */
interface RegisterRepository {
    /**
     * Creates a new user account with the provided details.
     *
     * @param firstName The user's first name.
     * @param lastName The user's last name.
     * @param nickname The user's display nickname.
     * @param dateBirth The user's date of birth in ISO-8601 format (e.g. `"1991-01-01"`).
     * @param email The user's email address.
     * @param password The user's chosen password.
     * @return [Result.success] with [Unit] on HTTP 201, or [Result.failure] with a descriptive exception.
     */
    suspend fun register(
        firstName: String,
        lastName: String,
        nickname: String,
        dateBirth: String,
        email: String,
        password: String,
    ): Result<Unit>
}
