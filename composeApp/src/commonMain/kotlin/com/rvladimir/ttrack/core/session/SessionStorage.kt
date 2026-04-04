package com.rvladimir.ttrack.core.session

/**
 * Platform-specific key-value store for persisting the authenticated session.
 *
 * Backed by SharedPreferences on Android and NSUserDefaults on iOS.
 * Both the token pair and the user profile are stored so that the app can
 * silently refresh the session and display user data after an app restart.
 */
expect class SessionStorage() {
    /** Persists the JWT [accessToken] and the opaque [refreshToken]. */
    fun saveTokens(
        accessToken: String,
        refreshToken: String,
    )

    /**
     * Retrieves the previously saved access token.
     *
     * @return The access token string, or `null` if no session exists.
     */
    fun getAccessToken(): String?

    /**
     * Retrieves the previously saved refresh token.
     *
     * @return The refresh token string, or `null` if no session exists.
     */
    fun getRefreshToken(): String?

    /**
     * Persists the authenticated user's profile information alongside the session.
     *
     * @param userId The user's unique identifier.
     * @param name The user's first name.
     * @param lastName The user's last name.
     * @param email The user's email address.
     */
    fun saveUserProfile(
        userId: Long,
        name: String,
        lastName: String,
        email: String,
    )

    /** @return The stored user ID, or `null` if no session exists. */
    fun getUserId(): Long?

    /** @return The stored user first name, or `null` if no session exists. */
    fun getUserName(): String?

    /** @return The stored user last name, or `null` if no session exists. */
    fun getUserLastName(): String?

    /** @return The stored user email, or `null` if no session exists. */
    fun getUserEmail(): String?

    /** Removes all stored session data (tokens + user profile), effectively ending the session. */
    fun clearTokens()
}
