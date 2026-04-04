package com.rvladimir.ttrack.auth

import com.rvladimir.ttrack.auth.domain.model.AuthResult
import com.rvladimir.ttrack.auth.domain.model.UserSession
import com.rvladimir.ttrack.auth.domain.repository.AuthRepository
import com.rvladimir.ttrack.auth.domain.usecase.GetSessionUseCase
import com.rvladimir.ttrack.auth.domain.usecase.GetUserProfileUseCase
import com.rvladimir.ttrack.auth.domain.usecase.LoginUseCase
import com.rvladimir.ttrack.auth.domain.usecase.RefreshTokenUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LoginUseCaseTest {
    // ── Fake repositories ─────────────────────────────────────────────────────

    private fun fakeSuccessRepository(
        accessToken: String = "access_tok_123",
        refreshToken: String = "refresh_tok_456",
        userId: Long = 42L,
        name: String = "John",
        lastName: String = "Doe",
        email: String = "john.doe@example.com",
    ) = object : AuthRepository {
        private var savedSession: UserSession? = null

        // Capture closure `email` before the `login` parameter shadows it.
        private val profileEmail = email

        override suspend fun login(
            email: String,
            password: String,
        ): Result<UserSession> =
            Result.success(
                UserSession(
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    userId = userId,
                    name = name,
                    lastName = lastName,
                    email = profileEmail,
                ),
            )

        override suspend fun refreshToken(refreshToken: String): Result<AuthResult> =
            Result.success(AuthResult(accessToken = "new_access", refreshToken = "new_refresh"))

        override fun saveSession(session: UserSession) {
            savedSession = session
        }

        override fun saveTokens(
            accessToken: String,
            refreshToken: String,
        ) = Unit

        override fun getAccessToken(): String? = savedSession?.accessToken ?: accessToken.takeIf { it.isNotEmpty() }

        override fun getRefreshToken(): String? = savedSession?.refreshToken ?: refreshToken.takeIf { it.isNotEmpty() }

        override fun getUserId(): Long? = savedSession?.userId

        override fun getUserName(): String? = savedSession?.name

        override fun getUserLastName(): String? = savedSession?.lastName

        override fun getUserEmail(): String? = savedSession?.email

        override fun clearTokens() = Unit
    }

    private val failingRepository =
        object : AuthRepository {
            override suspend fun login(
                email: String,
                password: String,
            ): Result<UserSession> = Result.failure(RuntimeException("Invalid credentials"))

            override suspend fun refreshToken(refreshToken: String): Result<AuthResult> =
                Result.failure(RuntimeException("Invalid refresh token"))

            override fun saveSession(session: UserSession) = Unit

            override fun saveTokens(
                accessToken: String,
                refreshToken: String,
            ) = Unit

            override fun getAccessToken(): String? = null

            override fun getRefreshToken(): String? = null

            override fun getUserId(): Long? = null

            override fun getUserName(): String? = null

            override fun getUserLastName(): String? = null

            override fun getUserEmail(): String? = null

            override fun clearTokens() = Unit
        }

    private val noSessionRepository =
        object : AuthRepository {
            override suspend fun login(
                email: String,
                password: String,
            ): Result<UserSession> = Result.failure(RuntimeException("Not expected"))

            override suspend fun refreshToken(refreshToken: String): Result<AuthResult> =
                Result.failure(RuntimeException("Not expected"))

            override fun saveSession(session: UserSession) = Unit

            override fun saveTokens(
                accessToken: String,
                refreshToken: String,
            ) = Unit

            override fun getAccessToken(): String? = null

            override fun getRefreshToken(): String? = null

            override fun getUserId(): Long? = null

            override fun getUserName(): String? = null

            override fun getUserLastName(): String? = null

            override fun getUserEmail(): String? = null

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
    fun `login returns success with full user profile`() =
        runTest {
            val useCase = LoginUseCase(fakeSuccessRepository())
            val result = useCase("user@example.com", "password123")
            assertTrue(result.isSuccess)
            assertEquals(42L, result.getOrNull()?.userId)
            assertEquals("John", result.getOrNull()?.name)
            assertEquals("Doe", result.getOrNull()?.lastName)
            assertEquals("john.doe@example.com", result.getOrNull()?.email)
        }

    @Test
    fun `login persists full session on success`() =
        runTest {
            val repo = fakeSuccessRepository()
            val useCase = LoginUseCase(repo)
            useCase("user@example.com", "password123")
            assertNotNull(repo.getUserId())
            assertEquals(42L, repo.getUserId())
            assertEquals("John", repo.getUserName())
            assertEquals("Doe", repo.getUserLastName())
            assertEquals("john.doe@example.com", repo.getUserEmail())
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
                    override fun getRefreshToken(): String = "expired_token"
                }
            val useCase = RefreshTokenUseCase(repositoryWithToken)
            val result = useCase()
            assertTrue(result.isFailure)
            assertEquals("Invalid refresh token", result.exceptionOrNull()?.message)
        }

    // ── GetUserProfileUseCase ─────────────────────────────────────────────────

    /**
     * A repository whose session fields are all pre-populated, simulating a user
     * who previously logged in and whose session was persisted.
     */
    private val fullSessionRepository =
        object : AuthRepository {
            override suspend fun login(
                email: String,
                password: String,
            ): Result<UserSession> = Result.failure(NotImplementedError())

            override suspend fun refreshToken(refreshToken: String): Result<AuthResult> =
                Result.failure(NotImplementedError())

            override fun saveSession(session: UserSession) = Unit

            override fun saveTokens(
                accessToken: String,
                refreshToken: String,
            ) = Unit

            override fun getAccessToken(): String = "access_tok"

            override fun getRefreshToken(): String = "refresh_tok"

            override fun getUserId(): Long = 42L

            override fun getUserName(): String = "John"

            override fun getUserLastName(): String = "Doe"

            override fun getUserEmail(): String = "john@example.com"

            override fun clearTokens() = Unit
        }

    @Test
    fun `GetUserProfileUseCase returns full session when all fields are stored`() {
        val useCase = GetUserProfileUseCase(fullSessionRepository)
        val session = useCase()
        assertNotNull(session)
        assertEquals("access_tok", session.accessToken)
        assertEquals("refresh_tok", session.refreshToken)
        assertEquals(42L, session.userId)
        assertEquals("John", session.name)
        assertEquals("Doe", session.lastName)
        assertEquals("john@example.com", session.email)
    }

    @Test
    fun `GetUserProfileUseCase returns null when access token is missing`() {
        val repo =
            object : AuthRepository by fullSessionRepository {
                override fun getAccessToken(): String? = null
            }
        assertNull(GetUserProfileUseCase(repo)())
    }

    @Test
    fun `GetUserProfileUseCase returns null when refresh token is missing`() {
        val repo =
            object : AuthRepository by fullSessionRepository {
                override fun getRefreshToken(): String? = null
            }
        assertNull(GetUserProfileUseCase(repo)())
    }

    @Test
    fun `GetUserProfileUseCase returns null when user id is missing`() {
        val repo =
            object : AuthRepository by fullSessionRepository {
                override fun getUserId(): Long? = null
            }
        assertNull(GetUserProfileUseCase(repo)())
    }

    @Test
    fun `GetUserProfileUseCase returns null when name is missing`() {
        val repo =
            object : AuthRepository by fullSessionRepository {
                override fun getUserName(): String? = null
            }
        assertNull(GetUserProfileUseCase(repo)())
    }

    @Test
    fun `GetUserProfileUseCase returns null when last name is missing`() {
        val repo =
            object : AuthRepository by fullSessionRepository {
                override fun getUserLastName(): String? = null
            }
        assertNull(GetUserProfileUseCase(repo)())
    }

    @Test
    fun `GetUserProfileUseCase returns null when email is missing`() {
        val repo =
            object : AuthRepository by fullSessionRepository {
                override fun getUserEmail(): String? = null
            }
        assertNull(GetUserProfileUseCase(repo)())
    }

    // ── GetSessionUseCase ─────────────────────────────────────────────────────

    @Test
    fun `GetSessionUseCase returns access token when session exists`() {
        val useCase = GetSessionUseCase(fullSessionRepository)
        assertEquals("access_tok", useCase())
    }

    @Test
    fun `GetSessionUseCase returns null when no session is stored`() {
        val useCase = GetSessionUseCase(noSessionRepository)
        assertNull(useCase())
    }
}
