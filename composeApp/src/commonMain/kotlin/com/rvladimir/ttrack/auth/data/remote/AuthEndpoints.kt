package com.rvladimir.ttrack.auth.data.remote

import com.rvladimir.ttrack.core.network.AppConfig

/**
 * Defines all authentication-related API endpoint URLs.
 *
 * Each property builds its full URL from [AppConfig.BASE_URL],
 * keeping endpoint definitions co-located and easy to maintain.
 */
object AuthEndpoints {
    val LOGIN = "${AppConfig.BASE_URL}/mobile-login"
}
