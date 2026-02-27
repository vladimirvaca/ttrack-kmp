package com.rvladimir.ttrack.registration.data.remote

import com.rvladimir.ttrack.core.network.AppConfig

/**
 * Defines all user-registration-related API endpoint URLs.
 *
 * Each property builds its full URL from [AppConfig.BASE_URL],
 * keeping endpoint definitions co-located and easy to maintain.
 */
object UserEndpoints {
    /** POST – create a new user account. Spec: `POST /user/create`. */
    val CREATE_USER = "${AppConfig.BASE_URL}/user/create"
}
