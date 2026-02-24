package com.rvladimir.ttrack.core.session

/**
 * Platform-specific key-value store for persisting the authenticated session token.
 *
 * Backed by SharedPreferences on Android and NSUserDefaults on iOS.
 */
expect class SessionStorage() {
    /** Persists [token] so the session survives app restarts. */
    fun saveToken(token: String)

    /**
     * Retrieves the previously saved token.
     *
     * @return The token string, or `null` if no session exists.
     */
    fun getToken(): String?

    /** Removes the stored token, effectively logging the user out. */
    fun clearToken()
}
