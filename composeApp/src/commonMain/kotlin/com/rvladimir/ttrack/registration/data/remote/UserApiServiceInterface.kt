package com.rvladimir.ttrack.registration.data.remote

import com.rvladimir.ttrack.registration.data.remote.dto.CreateUserDto
import io.ktor.client.statement.HttpResponse

/**
 * Contract for the user remote API service.
 * Allows faking in tests without spinning up a real Ktor engine.
 */
interface UserApiServiceInterface {
    /**
     * Calls `POST /user/create`.
     *
     * @param dto The user creation payload.
     * @return The raw [HttpResponse] (HTTP 201 on success).
     * @throws io.ktor.client.plugins.ClientRequestException on 4xx errors.
     * @throws io.ktor.client.plugins.ServerResponseException on 5xx errors.
     */
    suspend fun createUser(dto: CreateUserDto): HttpResponse
}
