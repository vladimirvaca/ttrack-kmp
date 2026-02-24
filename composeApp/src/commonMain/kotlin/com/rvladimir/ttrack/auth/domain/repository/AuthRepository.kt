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

    /** Persists [token] so the session survives app restarts. */
    fun saveToken(token: String)

    /**
     * Retrieves the currently stored session token.
     *
     * @return The token string, or `null` if the user is not logged in.
     */
    fun getToken(): String?

    /** Removes the stored token, effectively logging the user out. */
    fun clearToken()
}
