package com.rvladimir.ttrack.auth.domain.model

/**
 * Represents the result of a login operation.
 *
 * @property accessToken The JWT access token returned on success.
 * @property tokenType The token type (e.g. "Bearer").
 */
data class AuthResult(
    val accessToken: String,
    val tokenType: String? = null,
)
