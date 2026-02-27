package com.rvladimir.ttrack.registration

import com.rvladimir.ttrack.registration.domain.repository.RegisterRepository
import com.rvladimir.ttrack.registration.domain.usecase.RegisterUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class RegisterUseCaseTest {
    // ── Fake repositories ─────────────────────────────────────────────────────

    private val successRepository =
        object : RegisterRepository {
            override suspend fun register(
                firstName: String,
                lastName: String,
                nickname: String,
                dateBirth: String,
                email: String,
                password: String,
            ): Result<Unit> = Result.success(Unit)
        }

    private val failingRepository =
        object : RegisterRepository {
            override suspend fun register(
                firstName: String,
                lastName: String,
                nickname: String,
                dateBirth: String,
                email: String,
                password: String,
            ): Result<Unit> = Result.failure(RuntimeException("Invalid registration data. Please check your details."))
        }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private suspend fun RegisterUseCase.invokeWithValid() =
        invoke(
            firstName = "Tony",
            lastName = "Stark",
            nickname = "IronMan",
            dateBirth = "1991-01-01",
            email = "tony.stark@gmail.com",
            password = "12345",
        )

    // ── Success path ──────────────────────────────────────────────────────────

    @Test
    fun `register returns success when all fields are valid`() =
        runTest {
            val useCase = RegisterUseCase(successRepository)
            val result = useCase.invokeWithValid()
            assertTrue(result.isSuccess)
        }

    // ── Validation – blank fields ─────────────────────────────────────────────

    @Test
    fun `register returns failure when firstName is blank`() =
        runTest {
            val useCase = RegisterUseCase(successRepository)
            val result =
                useCase(
                    firstName = " ",
                    lastName = "Stark",
                    nickname = "IronMan",
                    dateBirth = "1991-01-01",
                    email = "tony@example.com",
                    password = "12345",
                )
            assertTrue(result.isFailure)
            assertIs<IllegalArgumentException>(result.exceptionOrNull())
        }

    @Test
    fun `register returns failure when lastName is blank`() =
        runTest {
            val useCase = RegisterUseCase(successRepository)
            val result =
                useCase(
                    firstName = "Tony",
                    lastName = "",
                    nickname = "IronMan",
                    dateBirth = "1991-01-01",
                    email = "tony@example.com",
                    password = "12345",
                )
            assertTrue(result.isFailure)
            assertIs<IllegalArgumentException>(result.exceptionOrNull())
        }

    @Test
    fun `register returns failure when nickname is blank`() =
        runTest {
            val useCase = RegisterUseCase(successRepository)
            val result =
                useCase(
                    firstName = "Tony",
                    lastName = "Stark",
                    nickname = "  ",
                    dateBirth = "1991-01-01",
                    email = "tony@example.com",
                    password = "12345",
                )
            assertTrue(result.isFailure)
            assertIs<IllegalArgumentException>(result.exceptionOrNull())
        }

    @Test
    fun `register returns failure when dateBirth is blank`() =
        runTest {
            val useCase = RegisterUseCase(successRepository)
            val result =
                useCase(
                    firstName = "Tony",
                    lastName = "Stark",
                    nickname = "IronMan",
                    dateBirth = "",
                    email = "tony@example.com",
                    password = "12345",
                )
            assertTrue(result.isFailure)
            assertIs<IllegalArgumentException>(result.exceptionOrNull())
        }

    @Test
    fun `register returns failure when email is blank`() =
        runTest {
            val useCase = RegisterUseCase(successRepository)
            val result =
                useCase(
                    firstName = "Tony",
                    lastName = "Stark",
                    nickname = "IronMan",
                    dateBirth = "1991-01-01",
                    email = "",
                    password = "12345",
                )
            assertTrue(result.isFailure)
            assertIs<IllegalArgumentException>(result.exceptionOrNull())
        }

    @Test
    fun `register returns failure when password is blank`() =
        runTest {
            val useCase = RegisterUseCase(successRepository)
            val result =
                useCase(
                    firstName = "Tony",
                    lastName = "Stark",
                    nickname = "IronMan",
                    dateBirth = "1991-01-01",
                    email = "tony@example.com",
                    password = "   ",
                )
            assertTrue(result.isFailure)
            assertIs<IllegalArgumentException>(result.exceptionOrNull())
        }

    // ── Error propagation ─────────────────────────────────────────────────────

    @Test
    fun `register propagates repository failure`() =
        runTest {
            val useCase = RegisterUseCase(failingRepository)
            val result = useCase.invokeWithValid()
            assertTrue(result.isFailure)
            assertEquals(
                "Invalid registration data. Please check your details.",
                result.exceptionOrNull()?.message,
            )
        }

    // ── Input trimming ────────────────────────────────────────────────────────

    @Test
    fun `register trims whitespace from fields before delegating`() =
        runTest {
            var capturedFirstName = ""
            var capturedEmail = ""
            val capturingRepository =
                object : RegisterRepository {
                    override suspend fun register(
                        firstName: String,
                        lastName: String,
                        nickname: String,
                        dateBirth: String,
                        email: String,
                        password: String,
                    ): Result<Unit> {
                        capturedFirstName = firstName
                        capturedEmail = email
                        return Result.success(Unit)
                    }
                }
            val useCase = RegisterUseCase(capturingRepository)
            useCase(
                firstName = "  Tony  ",
                lastName = "Stark",
                nickname = "IronMan",
                dateBirth = "1991-01-01",
                email = "  tony@example.com  ",
                password = "12345",
            )
            assertEquals("Tony", capturedFirstName)
            assertEquals("tony@example.com", capturedEmail)
        }
}
