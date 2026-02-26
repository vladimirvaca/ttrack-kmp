package com.rvladimir.ttrack.core.session

import platform.Foundation.NSUserDefaults

private const val KEY_ACCESS_TOKEN = "auth_access_token"
private const val KEY_REFRESH_TOKEN = "auth_refresh_token"

/** iOS implementation of [SessionStorage] backed by [NSUserDefaults]. */
actual class SessionStorage {
    private val defaults = NSUserDefaults.standardUserDefaults

    actual fun saveTokens(
        accessToken: String,
        refreshToken: String,
    ) {
        defaults.setObject(accessToken, KEY_ACCESS_TOKEN)
        defaults.setObject(refreshToken, KEY_REFRESH_TOKEN)
    }

    actual fun getAccessToken(): String? = defaults.stringForKey(KEY_ACCESS_TOKEN)

    actual fun getRefreshToken(): String? = defaults.stringForKey(KEY_REFRESH_TOKEN)

    actual fun clearTokens() {
        defaults.removeObjectForKey(KEY_ACCESS_TOKEN)
        defaults.removeObjectForKey(KEY_REFRESH_TOKEN)
    }
}
