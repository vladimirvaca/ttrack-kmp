package com.rvladimir.ttrack.auth

import com.rvladimir.ttrack.auth.domain.model.AuthResult
import com.rvladimir.ttrack.auth.domain.repository.AuthRepository
import com.rvladimir.ttrack.auth.domain.usecase.LoginUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class LoginUseCaseTest {
    private val successRepository =
        object : AuthRepository {
            override suspend fun login(
                email: String,
                password: String,
            ): Result<AuthResult> = Result.success(AuthResult(token = "tok_123", userId = "user_42"))
        }

    private val failingRepository =
        object : AuthRepository {
            override suspend fun login(
                email: String,
                password: String,
            ): Result<AuthResult> = Result.failure(RuntimeException("Invalid credentials"))
        }

    @Test
    fun `login returns success when repository succeeds`() =
        runTest {
            val useCase = LoginUseCase(successRepository)
            val result = useCase("user@example.com", "password123")
            assertTrue(result.isSuccess)
            assertEquals("tok_123", result.getOrNull()?.token)
        }

    @Test
    fun `login returns failure for blank email`() =
        runTest {
            val useCase = LoginUseCase(successRepository)
            val result = useCase("", "password123")
            assertTrue(result.isFailure)
            assertIs<IllegalArgumentException>(result.exceptionOrNull())
        }

    @Test
    fun `login returns failure for blank password`() =
        runTest {
            val useCase = LoginUseCase(successRepository)
            val result = useCase("user@example.com", "")
            assertTrue(result.isFailure)
            assertIs<IllegalArgumentException>(result.exceptionOrNull())
        }

    @Test
    fun `login propagates repository failure`() =
        runTest {
            val useCase = LoginUseCase(failingRepository)
            val result = useCase("user@example.com", "wrongpassword")
            assertTrue(result.isFailure)
            assertEquals("Invalid credentials", result.exceptionOrNull()?.message)
        }
}
