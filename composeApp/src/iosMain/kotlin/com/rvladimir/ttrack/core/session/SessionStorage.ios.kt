package com.rvladimir.ttrack.core.session

import platform.Foundation.NSUserDefaults

private const val KEY_ACCESS_TOKEN = "auth_access_token"
private const val KEY_REFRESH_TOKEN = "auth_refresh_token"
private const val KEY_USER_ID = "auth_user_id"
private const val KEY_USER_NAME = "auth_user_name"
private const val KEY_USER_LAST_NAME = "auth_user_last_name"
private const val KEY_USER_EMAIL = "auth_user_email"

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

    actual fun saveUserProfile(
        userId: Long,
        name: String,
        lastName: String,
        email: String,
    ) {
        defaults.setObject(userId.toString(), KEY_USER_ID)
        defaults.setObject(name, KEY_USER_NAME)
        defaults.setObject(lastName, KEY_USER_LAST_NAME)
        defaults.setObject(email, KEY_USER_EMAIL)
    }

    actual fun getUserId(): Long? = defaults.stringForKey(KEY_USER_ID)?.toLongOrNull()

    actual fun getUserName(): String? = defaults.stringForKey(KEY_USER_NAME)

    actual fun getUserLastName(): String? = defaults.stringForKey(KEY_USER_LAST_NAME)

    actual fun getUserEmail(): String? = defaults.stringForKey(KEY_USER_EMAIL)

    actual fun clearTokens() {
        defaults.removeObjectForKey(KEY_ACCESS_TOKEN)
        defaults.removeObjectForKey(KEY_REFRESH_TOKEN)
        defaults.removeObjectForKey(KEY_USER_ID)
        defaults.removeObjectForKey(KEY_USER_NAME)
        defaults.removeObjectForKey(KEY_USER_LAST_NAME)
        defaults.removeObjectForKey(KEY_USER_EMAIL)
    }
}
