package com.rvladimir.ttrack.auth.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Request body sent to the login endpoint. */
@Serializable
data class LoginRequestDto(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
)

/** Response body received from the login endpoint. */
@Serializable
data class LoginResponseDto(
    @SerialName("token") val token: String,
    @SerialName("user_id") val userId: String,
)
