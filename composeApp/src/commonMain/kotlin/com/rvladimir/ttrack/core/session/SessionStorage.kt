package com.rvladimir.ttrack.core.session

/**
 * Platform-specific key-value store for persisting the authenticated session tokens.
 *
 * Backed by SharedPreferences on Android and NSUserDefaults on iOS.
 * Both the access token and the refresh token are stored so that the app can
 * silently refresh the session after an app restart.
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

    /** Removes both stored tokens, effectively ending the session. */
    fun clearTokens()
}
