package com.rvladimir.ttrack.auth.data.remote

import com.rvladimir.ttrack.auth.data.remote.dto.LoginRequestDto
import com.rvladimir.ttrack.auth.data.remote.dto.RefreshTokenRequestDto
import com.rvladimir.ttrack.auth.data.remote.dto.TokenResponseDto
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
     * Calls the mobile login endpoint ([AuthEndpoints.MOBILE_LOGIN]).
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @return [TokenResponseDto] containing both access and refresh tokens.
     * @throws io.ktor.client.plugins.ClientRequestException on 4xx errors.
     * @throws io.ktor.client.plugins.ServerResponseException on 5xx errors.
     */
    suspend fun login(
        email: String,
        password: String,
    ): TokenResponseDto =
        httpClient
            .post(AuthEndpoints.MOBILE_LOGIN) {
                contentType(ContentType.Application.Json)
                setBody(LoginRequestDto(email = email, password = password))
            }.body()

    /**
     * Calls the token refresh endpoint ([AuthEndpoints.MOBILE_REFRESH]).
     *
     * Sends the current refresh token and receives a new pair of
     * access + refresh tokens (token rotation).
     *
     * @param refreshToken The refresh token issued during the last login or refresh.
     * @return [TokenResponseDto] containing the new access and refresh tokens.
     * @throws io.ktor.client.plugins.ClientRequestException on 401 (expired/invalid refresh token).
     * @throws io.ktor.client.plugins.ServerResponseException on 5xx errors.
     */
    suspend fun refreshToken(refreshToken: String): TokenResponseDto =
        httpClient
            .post(AuthEndpoints.MOBILE_REFRESH) {
                contentType(ContentType.Application.Json)
                setBody(RefreshTokenRequestDto(refreshToken = refreshToken))
            }.body()
}
