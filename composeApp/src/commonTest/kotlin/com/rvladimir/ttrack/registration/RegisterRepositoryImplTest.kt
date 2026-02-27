package com.rvladimir.ttrack.registration

import com.rvladimir.ttrack.registration.data.remote.UserApiServiceInterface
import com.rvladimir.ttrack.registration.data.remote.dto.CreateUserDto
import com.rvladimir.ttrack.registration.data.repository.RegisterRepositoryImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RegisterRepositoryImplTest {
    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Builds a [UserApiServiceInterface] backed by a [MockEngine] that always
     * responds with [statusCode] and an empty body.
     */
    private fun serviceReturning(statusCode: HttpStatusCode): UserApiServiceInterface {
        val engine =
            MockEngine {
                respond(
                    content = "",
                    status = statusCode,
                    headers = headersOf("Content-Type", ContentType.Application.Json.toString()),
                )
            }
        val client =
            HttpClient(engine) {
                install(ContentNegotiation) {
                    json(Json { ignoreUnknownKeys = true })
                }
            }
        return object : UserApiServiceInterface {
            override suspend fun createUser(dto: CreateUserDto): HttpResponse =
                client.post("http://localhost/user/create") {
                    contentType(ContentType.Application.Json)
                    setBody("{}")
                }
        }
    }

    private fun serviceThrowing(exception: Exception): UserApiServiceInterface =
        object : UserApiServiceInterface {
            override suspend fun createUser(dto: CreateUserDto): HttpResponse = throw exception
        }

    private suspend fun RegisterRepositoryImpl.registerValid(): Result<Unit> =
        register(
            firstName = "Tony",
            lastName = "Stark",
            nickname = "IronMan",
            dateBirth = "1991-01-01",
            email = "tony@example.com",
            password = "12345",
        )

    // ── HTTP 201 ──────────────────────────────────────────────────────────────

    @Test
    fun `register returns success on HTTP 201`() =
        runTest {
            val repo = RegisterRepositoryImpl(serviceReturning(HttpStatusCode.Created))
            assertTrue(repo.registerValid().isSuccess)
        }

    // ── Non-201 status ────────────────────────────────────────────────────────

    @Test
    fun `register returns failure on HTTP 200`() =
        runTest {
            val repo = RegisterRepositoryImpl(serviceReturning(HttpStatusCode.OK))
            val result = repo.registerValid()
            assertTrue(result.isFailure)
            assertEquals("Registration failed: HTTP 200", result.exceptionOrNull()?.message)
        }

    @Test
    fun `register returns failure on HTTP 500`() =
        runTest {
            val repo = RegisterRepositoryImpl(serviceReturning(HttpStatusCode.InternalServerError))
            val result = repo.registerValid()
            assertTrue(result.isFailure)
            assertEquals("Registration failed: HTTP 500", result.exceptionOrNull()?.message)
        }

    // ── Network / generic exceptions ──────────────────────────────────────────

    @Test
    fun `register returns failure when network throws`() =
        runTest {
            val repo = RegisterRepositoryImpl(serviceThrowing(RuntimeException("No internet")))
            val result = repo.registerValid()
            assertTrue(result.isFailure)
            assertEquals("No internet", result.exceptionOrNull()?.message)
        }
}
