package com.rvladimir.ttrack.core.session

import android.content.Context

private const val PREFS_NAME = "ttrack_session"
private const val KEY_ACCESS_TOKEN = "auth_access_token"
private const val KEY_REFRESH_TOKEN = "auth_refresh_token"

/**
 * Holds the application [Context] injected from [com.rvladimir.ttrack.MainActivity].
 * Using `applicationContext` prevents activity leaks.
 */
internal object AppContextHolder {
    lateinit var appContext: Context
}

/** Android implementation of [SessionStorage] backed by [android.content.SharedPreferences]. */
actual class SessionStorage {
    private val prefs
        get() =
            AppContextHolder.appContext
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    actual fun saveTokens(
        accessToken: String,
        refreshToken: String,
    ) {
        prefs
            .edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .apply()
    }

    actual fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)

    actual fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)

    actual fun clearTokens() {
        prefs
            .edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .apply()
    }
}
