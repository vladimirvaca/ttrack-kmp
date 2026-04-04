package com.rvladimir.ttrack.auth.domain.model

/**
 * Represents the full user session returned by a successful login.
 *
 * Contains both authentication tokens and basic user profile information.
 *
 * @property accessToken The JWT access token used to authorise API requests.
 * @property refreshToken The opaque refresh token used to obtain a new [accessToken] when it expires.
 * @property tokenType The token scheme reported by the server (e.g. "Bearer").
 * @property userId The authenticated user's unique identifier.
 * @property name The user's first name.
 * @property lastName The user's last name.
 * @property email The user's email address.
 */
data class UserSession(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String? = null,
    val userId: Long,
    val name: String,
    val lastName: String,
    val email: String,
)
