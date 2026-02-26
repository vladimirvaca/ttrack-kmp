package com.rvladimir.ttrack.auth.domain.model

/**
 * Represents the token pair returned by a successful login or token refresh.
 *
 * @property accessToken The JWT access token used to authorise API requests.
 * @property refreshToken The opaque refresh token used to obtain a new [accessToken] when it expires.
 * @property tokenType The token scheme reported by the server (e.g. "Bearer").
 */
data class AuthResult(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String? = null,
)
