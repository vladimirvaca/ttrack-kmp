package com.rvladimir.ttrack.auth.domain.repository

import com.rvladimir.ttrack.auth.domain.model.AuthResult

/**
 * Contract for authentication operations.
 * Implementations live in the data layer.
 */
interface AuthRepository {
    /**
     * Attempts to log in with the given credentials.
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @return [Result] wrapping [AuthResult] on success or an exception on failure.
     */
    suspend fun login(
        email: String,
        password: String,
    ): Result<AuthResult>
}
