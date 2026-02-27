package com.rvladimir.ttrack.registration.data.repository

import com.rvladimir.ttrack.registration.data.remote.UserApiServiceInterface
import com.rvladimir.ttrack.registration.data.remote.dto.CreateUserDto
import com.rvladimir.ttrack.registration.domain.repository.RegisterRepository
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode

/**
 * Concrete implementation of [RegisterRepository] that delegates to [UserApiServiceInterface]
 * for the `POST /user/create` network call.
 *
 * @property apiService The remote API service for user operations.
 */
class RegisterRepositoryImpl(
    private val apiService: UserApiServiceInterface,
) : RegisterRepository {
    override suspend fun register(
        firstName: String,
        lastName: String,
        nickname: String,
        dateBirth: String,
        email: String,
        password: String,
    ): Result<Unit> =
        try {
            val dto =
                CreateUserDto(
                    name = firstName,
                    lastname = lastName,
                    nickname = nickname,
                    dateBirth = dateBirth,
                    email = email,
                    password = password,
                )
            val response = apiService.createUser(dto)
            if (response.status == HttpStatusCode.Created) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Registration failed: HTTP ${response.status.value}"))
            }
        } catch (e: ClientRequestException) {
            val message =
                when (e.response.status) {
                    HttpStatusCode.BadRequest -> "Invalid registration data. Please check your details."
                    else -> "Registration failed: ${e.response.status.description}"
                }
            Result.failure(Exception(message))
        } catch (e: Exception) {
            Result.failure(e)
        }
}
