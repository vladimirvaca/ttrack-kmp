package com.rvladimir.ttrack.registration.data.remote

import com.rvladimir.ttrack.registration.data.remote.dto.CreateUserDto
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Low-level HTTP service for user-related endpoints.
 *
 * Endpoint URLs are resolved from [UserEndpoints] so that URL paths
 * are defined in a single, dedicated place.
 *
 * @property httpClient The configured Ktor [HttpClient].
 */
class UserApiService(
    private val httpClient: HttpClient,
) : UserApiServiceInterface {
    /**
     * Calls the create-user endpoint ([UserEndpoints.CREATE_USER]).
     *
     * @param dto The user creation payload as defined by the API spec.
     * @return The raw [HttpResponse] (HTTP 201 on success).
     * @throws io.ktor.client.plugins.ClientRequestException on 4xx errors.
     * @throws io.ktor.client.plugins.ServerResponseException on 5xx errors.
     */
    override suspend fun createUser(dto: CreateUserDto): HttpResponse =
        httpClient.post(UserEndpoints.CREATE_USER) {
            contentType(ContentType.Application.Json)
            setBody(dto)
        }
}
