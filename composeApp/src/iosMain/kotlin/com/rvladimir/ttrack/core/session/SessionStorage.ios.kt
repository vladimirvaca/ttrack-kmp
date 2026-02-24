package com.rvladimir.ttrack.core.session

import platform.Foundation.NSUserDefaults

private const val KEY_TOKEN = "auth_token"

/** iOS implementation using [NSUserDefaults]. */
actual class SessionStorage {
    private val defaults = NSUserDefaults.standardUserDefaults

    actual fun saveToken(token: String) {
        defaults.setObject(token, KEY_TOKEN)
    }

    actual fun getToken(): String? = defaults.stringForKey(KEY_TOKEN)

    actual fun clearToken() {
        defaults.removeObjectForKey(KEY_TOKEN)
    }
}
