package com.rvladimir.ttrack.auth.data.remote

import com.rvladimir.ttrack.auth.data.remote.dto.LoginRequestDto
import com.rvladimir.ttrack.auth.data.remote.dto.LoginResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Low-level HTTP service for authentication endpoints.
 *
 * Endpoints are resolved from [AuthEndpoints] so that URL paths
 * are defined in a single, dedicated place.
 *
 * @property httpClient The configured Ktor [HttpClient].
 */
class AuthApiService(
    private val httpClient: HttpClient,
) {
    /**
     * Calls the login endpoint ([AuthEndpoints.LOGIN]).
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @return [LoginResponseDto] parsed from the server response.
     * @throws io.ktor.client.plugins.ClientRequestException on 4xx errors.
     * @throws io.ktor.client.plugins.ServerResponseException on 5xx errors.
     */
    suspend fun login(
        email: String,
        password: String,
    ): LoginResponseDto =
        httpClient
            .post(AuthEndpoints.LOGIN) {
                contentType(ContentType.Application.Json)
                setBody(LoginRequestDto(email = email, password = password))
            }.body()
}
