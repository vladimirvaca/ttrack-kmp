package com.rvladimir.ttrack.registration.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request body sent to [POST /user/create].
 * Matches `CreateUserDTO` in the API spec (ttrack-be-0.3.5).
 */
@Serializable
data class CreateUserDto(
    @SerialName("name") val name: String,
    @SerialName("lastname") val lastname: String,
    @SerialName("nickname") val nickname: String,
    /** ISO-8601 date string, e.g. `"1991-01-01"`. */
    @SerialName("dateBirth") val dateBirth: String,
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
)
