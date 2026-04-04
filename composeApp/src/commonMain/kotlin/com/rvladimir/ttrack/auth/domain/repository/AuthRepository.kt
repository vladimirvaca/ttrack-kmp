package com.rvladimir.ttrack.auth.domain.repository

import com.rvladimir.ttrack.auth.domain.model.AuthResult
import com.rvladimir.ttrack.auth.domain.model.UserSession

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
     * @return [Result] wrapping [UserSession] on success or an exception on failure.
     */
    suspend fun login(
        email: String,
        password: String,
    ): Result<UserSession>

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
     * Persists the full user session (tokens + profile) so it survives app restarts.
     *
     * @param session The [UserSession] returned by a successful login.
     */
    fun saveSession(session: UserSession)

    /**
     * Persists only the token pair. Used during silent token refresh where
     * no new profile data is returned by the server.
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

    /** @return The stored user ID, or `null` if no session exists. */
    fun getUserId(): Long?

    /** @return The stored user first name, or `null` if no session exists. */
    fun getUserName(): String?

    /** @return The stored user last name, or `null` if no session exists. */
    fun getUserLastName(): String?

    /** @return The stored user email, or `null` if no session exists. */
    fun getUserEmail(): String?

    /** Removes all stored session data (tokens + user profile), effectively logging the user out. */
    fun clearTokens()
}
