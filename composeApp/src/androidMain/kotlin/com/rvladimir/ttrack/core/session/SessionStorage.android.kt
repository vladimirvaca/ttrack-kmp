package com.rvladimir.ttrack.core.session

import android.content.Context

private const val PREFS_NAME = "ttrack_session"
private const val KEY_TOKEN = "auth_token"

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

    actual fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    actual fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    actual fun clearToken() {
        prefs.edit().remove(KEY_TOKEN).apply()
    }
}
