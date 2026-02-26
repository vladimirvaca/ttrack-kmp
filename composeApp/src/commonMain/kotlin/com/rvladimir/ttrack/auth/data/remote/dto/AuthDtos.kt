package com.rvladimir.ttrack.auth.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Request body sent to the mobile-login endpoint. Matches `LoginDTO` in the API spec. */
@Serializable
data class LoginRequestDto(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
)

/**
 * Response body received from both the mobile-login and mobile-refresh endpoints.
 * Matches `TokenResponseDTO` in the API spec.
 */
@Serializable
data class TokenResponseDto(
    @SerialName("accessToken") val accessToken: String,
    @SerialName("tokenType") val tokenType: String? = null,
    @SerialName("refreshToken") val refreshToken: String,
)

/** Request body sent to the mobile-refresh endpoint. Matches `RefreshTokenRequestDTO` in the API spec. */
@Serializable
data class RefreshTokenRequestDto(
    @SerialName("refreshToken") val refreshToken: String,
)
