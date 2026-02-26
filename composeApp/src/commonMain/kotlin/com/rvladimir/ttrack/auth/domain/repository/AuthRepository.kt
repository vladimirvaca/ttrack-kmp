package com.rvladimir.ttrack.auth.domain.repository

import com.rvladimir.ttrack.auth.domain.model.AuthResult

/**
 * Contract for authentication operations.
 * Implementations live in the data layer.
 */
interface AuthRepository {
    /**
     * Authenticates the user with the given credentials.
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @return [Result] wrapping [AuthResult] on success or an exception on failure.
     */
    suspend fun login(
        email: String,
        password: String,
    ): Result<AuthResult>

    /**
     * Exchanges a refresh token for a new access + refresh token pair.
     *
     * The server rotates the refresh token on each call, so callers must
     * persist both new tokens immediately after a successful response.
     *
     * @param refreshToken The refresh token issued during the last login or refresh.
     * @return [Result] wrapping [AuthResult] on success, or an exception (e.g. 401) on failure.
     */
    suspend fun refreshToken(refreshToken: String): Result<AuthResult>

    /**
     * Persists the full token pair so the session survives app restarts.
     *
     * @param accessToken The JWT access token.
     * @param refreshToken The opaque refresh token.
     */
    fun saveTokens(
        accessToken: String,
        refreshToken: String,
    )

    /**
     * Retrieves the currently stored access token.
     *
     * @return The access token string, or `null` if the user is not logged in.
     */
    fun getAccessToken(): String?

    /**
     * Retrieves the currently stored refresh token.
     *
     * @return The refresh token string, or `null` if no session exists.
     */
    fun getRefreshToken(): String?

    /** Removes all stored tokens, effectively logging the user out. */
    fun clearTokens()
}
