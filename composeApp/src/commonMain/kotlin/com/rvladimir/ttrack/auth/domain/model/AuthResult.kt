package com.rvladimir.ttrack.auth.domain.model

/**
 * Represents the result of a login operation.
 *
 * @property token The bearer token returned on success.
 * @property userId The authenticated user's identifier.
 */
data class AuthResult(
    val token: String,
    val userId: String,
)
