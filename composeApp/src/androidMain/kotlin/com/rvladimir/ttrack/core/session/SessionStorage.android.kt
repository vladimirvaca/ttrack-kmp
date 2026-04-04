package com.rvladimir.ttrack.core.session

import android.content.Context
import androidx.core.content.edit

private const val PREFS_NAME = "ttrack_session"
private const val KEY_ACCESS_TOKEN = "auth_access_token"
private const val KEY_REFRESH_TOKEN = "auth_refresh_token"
private const val KEY_USER_ID = "auth_user_id"
private const val KEY_USER_NAME = "auth_user_name"
private const val KEY_USER_LAST_NAME = "auth_user_last_name"
private const val KEY_USER_EMAIL = "auth_user_email"

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
        prefs.edit {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
        }
    }

    actual fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)

    actual fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)

    actual fun saveUserProfile(
        userId: Long,
        name: String,
        lastName: String,
        email: String,
    ) {
        prefs.edit {
            putString(KEY_USER_ID, userId.toString())
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_LAST_NAME, lastName)
            putString(KEY_USER_EMAIL, email)
        }
    }

    actual fun getUserId(): Long? = prefs.getString(KEY_USER_ID, null)?.toLongOrNull()

    actual fun getUserName(): String? = prefs.getString(KEY_USER_NAME, null)

    actual fun getUserLastName(): String? = prefs.getString(KEY_USER_LAST_NAME, null)

    actual fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)

    actual fun clearTokens() {
        prefs.edit {
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_REFRESH_TOKEN)
            remove(KEY_USER_ID)
            remove(KEY_USER_NAME)
            remove(KEY_USER_LAST_NAME)
            remove(KEY_USER_EMAIL)
        }
    }
}
