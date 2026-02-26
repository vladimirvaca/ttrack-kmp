package com.rvladimir.ttrack.auth

import com.rvladimir.ttrack.auth.domain.model.AuthResult
import com.rvladimir.ttrack.auth.domain.repository.AuthRepository
import com.rvladimir.ttrack.auth.domain.usecase.LoginUseCase
import com.rvladimir.ttrack.auth.domain.usecase.RefreshTokenUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class LoginUseCaseTest {
    private fun fakeSuccessRepository(
        accessToken: String = "access_tok_123",
        refreshToken: String = "refresh_tok_456",
    ) = object : AuthRepository {
        override suspend fun login(
            email: String,
            password: String,
        ): Result<AuthResult> = Result.success(AuthResult(accessToken = accessToken, refreshToken = refreshToken))

        override suspend fun refreshToken(refreshToken: String): Result<AuthResult> =
            Result.success(AuthResult(accessToken = "new_access", refreshToken = "new_refresh"))

        override fun saveTokens(
            accessToken: String,
            refreshToken: String,
        ) = Unit

        override fun getAccessToken(): String? = accessToken.takeIf { it.isNotEmpty() }

        override fun getRefreshToken(): String? = refreshToken.takeIf { it.isNotEmpty() }

        override fun clearTokens() = Unit
    }

    private val failingRepository =
        object : AuthRepository {
            override suspend fun login(
                email: String,
                password: String,
            ): Result<AuthResult> = Result.failure(RuntimeException("Invalid credentials"))

            override suspend fun refreshToken(refreshToken: String): Result<AuthResult> =
                Result.failure(RuntimeException("Invalid refresh token"))

            override fun saveTokens(
                accessToken: String,
                refreshToken: String,
            ) = Unit

            override fun getAccessToken(): String? = null

            override fun getRefreshToken(): String? = null

            override fun clearTokens() = Unit
        }

    private val noSessionRepository =
        object : AuthRepository {
            override suspend fun login(
                email: String,
                password: String,
            ): Result<AuthResult> = Result.failure(RuntimeException("Not expected"))

            override suspend fun refreshToken(refreshToken: String): Result<AuthResult> =
                Result.failure(RuntimeException("Not expected"))

            override fun saveTokens(
                accessToken: String,
                refreshToken: String,
            ) = Unit

            override fun getAccessToken(): String? = null

            override fun getRefreshToken(): String? = null

            override fun clearTokens() = Unit
        }

    // ── LoginUseCase ──────────────────────────────────────────────────────────

    @Test
    fun `login returns success with both tokens when repository succeeds`() =
        runTest {
            val useCase = LoginUseCase(fakeSuccessRepository())
            val result = useCase("user@example.com", "password123")
            assertTrue(result.isSuccess)
            assertEquals("access_tok_123", result.getOrNull()?.accessToken)
            assertEquals("refresh_tok_456", result.getOrNull()?.refreshToken)
        }

    @Test
    fun `login returns failure for blank email`() =
        runTest {
            val useCase = LoginUseCase(fakeSuccessRepository())
            val result = useCase("", "password123")
            assertTrue(result.isFailure)
            assertIs<IllegalArgumentException>(result.exceptionOrNull())
        }

    @Test
    fun `login returns failure for blank password`() =
        runTest {
            val useCase = LoginUseCase(fakeSuccessRepository())
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

    // ── RefreshTokenUseCase ───────────────────────────────────────────────────

    @Test
    fun `refresh returns new token pair when refresh token is stored`() =
        runTest {
            val useCase = RefreshTokenUseCase(fakeSuccessRepository())
            val result = useCase()
            assertTrue(result.isSuccess)
            assertEquals("new_access", result.getOrNull()?.accessToken)
            assertEquals("new_refresh", result.getOrNull()?.refreshToken)
        }

    @Test
    fun `refresh fails with IllegalStateException when no refresh token is stored`() =
        runTest {
            val useCase = RefreshTokenUseCase(noSessionRepository)
            val result = useCase()
            assertTrue(result.isFailure)
            assertIs<IllegalStateException>(result.exceptionOrNull())
        }

    @Test
    fun `refresh propagates repository failure`() =
        runTest {
            val repositoryWithToken =
                object : AuthRepository by failingRepository {
                    override fun getRefreshToken(): String? = "expired_token".takeIf { true }
                }
            val useCase = RefreshTokenUseCase(repositoryWithToken)
            val result = useCase()
            assertTrue(result.isFailure)
            assertEquals("Invalid refresh token", result.exceptionOrNull()?.message)
        }
}
