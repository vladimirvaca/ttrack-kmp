package com.rvladimir.ttrack.auth

import com.rvladimir.ttrack.auth.data.remote.dto.TokenResponseDto
import com.rvladimir.ttrack.auth.domain.model.AuthResult
import com.rvladimir.ttrack.auth.domain.repository.AuthRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for the auth repository behaviour.
 *
 * Because [com.rvladimir.ttrack.auth.data.remote.AuthApiService] is a concrete
 * class and [com.rvladimir.ttrack.core.session.SessionStorage] is an `expect class`,
 * both are replaced by lightweight hand-rolled fakes that implement [AuthRepository]
 * directly—mirroring the repository's own logic—so no network or platform storage
 * is needed.
 */
class AuthRepositoryImplTest {
    // ── Fake token DTOs ───────────────────────────────────────────────────────

    private val defaultTokenDto =
        TokenResponseDto(
            accessToken = "access_tok",
            refreshToken = "refresh_tok",
            tokenType = "Bearer",
        )

    private val refreshedTokenDto =
        TokenResponseDto(
            accessToken = "new_access",
            refreshToken = "new_refresh",
            tokenType = "Bearer",
        )

    // ── In-memory session storage ─────────────────────────────────────────────

    private class InMemorySessionStorage {
        private var accessToken: String? = null
        private var refreshToken: String? = null

        fun saveTokens(
            accessToken: String,
            refreshToken: String,
        ) {
            this.accessToken = accessToken
            this.refreshToken = refreshToken
        }

        fun getAccessToken(): String? = accessToken

        fun getRefreshToken(): String? = refreshToken

        fun clearTokens() {
            accessToken = null
            refreshToken = null
        }
    }

    // ── Repository factory ────────────────────────────────────────────────────

    /**
     * Builds an [AuthRepository] that mirrors [com.rvladimir.ttrack.auth.data.repository.AuthRepositoryImpl]
     * behaviour, delegating token persistence to [storage] and API calls to the
     * provided lambdas.
     */
    private fun buildRepo(
        storage: InMemorySessionStorage = InMemorySessionStorage(),
        loginFn: suspend (String, String) -> TokenResponseDto,
        refreshFn: suspend (String) -> TokenResponseDto = { throw NotImplementedError() },
    ): AuthRepository =
        object : AuthRepository {
            override suspend fun login(
                email: String,
                password: String,
            ): Result<AuthResult> =
                try {
                    val dto = loginFn(email, password)
                    Result.success(AuthResult(dto.accessToken, dto.refreshToken, dto.tokenType))
                } catch (e: Exception) {
                    Result.failure(e)
                }

            override suspend fun refreshToken(refreshToken: String): Result<AuthResult> =
                runCatching {
                    val dto = refreshFn(refreshToken)
                    AuthResult(dto.accessToken, dto.refreshToken, dto.tokenType)
                }

            override fun saveTokens(
                accessToken: String,
                refreshToken: String,
            ) = storage.saveTokens(accessToken, refreshToken)

            override fun getAccessToken(): String? = storage.getAccessToken()

            override fun getRefreshToken(): String? = storage.getRefreshToken()

            override fun clearTokens() = storage.clearTokens()
        }

    // ── login() ───────────────────────────────────────────────────────────────

    @Test
    fun `login returns success with correct token pair`() =
        runTest {
            val repo = buildRepo(loginFn = { _, _ -> defaultTokenDto })
            val result = repo.login("user@example.com", "password123")
            assertTrue(result.isSuccess)
            assertEquals("access_tok", result.getOrNull()?.accessToken)
            assertEquals("refresh_tok", result.getOrNull()?.refreshToken)
            assertEquals("Bearer", result.getOrNull()?.tokenType)
        }

    @Test
    fun `login maps network exception to Result failure`() =
        runTest {
            val repo = buildRepo(loginFn = { _, _ -> throw RuntimeException("Connection refused") })
            val result = repo.login("user@example.com", "password123")
            assertTrue(result.isFailure)
            assertEquals("Connection refused", result.exceptionOrNull()?.message)
        }

    @Test
    fun `login passes credentials through to the api service unchanged`() =
        runTest {
            var capturedEmail: String? = null
            var capturedPassword: String? = null
            val repo =
                buildRepo(
                    loginFn = { email, password ->
                        capturedEmail = email
                        capturedPassword = password
                        defaultTokenDto
                    },
                )
            repo.login("user@example.com", "secret")
            assertEquals("user@example.com", capturedEmail)
            assertEquals("secret", capturedPassword)
        }

    // ── refreshToken() ────────────────────────────────────────────────────────

    @Test
    fun `refreshToken returns new token pair on success`() =
        runTest {
            val repo = buildRepo(loginFn = { _, _ -> defaultTokenDto }, refreshFn = { refreshedTokenDto })
            val result = repo.refreshToken("old_refresh_tok")
            assertTrue(result.isSuccess)
            assertEquals("new_access", result.getOrNull()?.accessToken)
            assertEquals("new_refresh", result.getOrNull()?.refreshToken)
        }

    @Test
    fun `refreshToken wraps api exception in Result failure`() =
        runTest {
            val repo =
                buildRepo(
                    loginFn = { _, _ -> defaultTokenDto },
                    refreshFn = { throw RuntimeException("Token expired") },
                )
            val result = repo.refreshToken("bad_token")
            assertTrue(result.isFailure)
            assertEquals("Token expired", result.exceptionOrNull()?.message)
        }

    // ── saveTokens / getAccessToken / getRefreshToken ─────────────────────────

    @Test
    fun `getAccessToken returns null when no tokens have been saved`() =
        runTest {
            val repo = buildRepo(loginFn = { _, _ -> defaultTokenDto })
            assertNull(repo.getAccessToken())
        }

    @Test
    fun `getRefreshToken returns null when no tokens have been saved`() =
        runTest {
            val repo = buildRepo(loginFn = { _, _ -> defaultTokenDto })
            assertNull(repo.getRefreshToken())
        }

    @Test
    fun `saveTokens persists both access and refresh tokens`() =
        runTest {
            val repo = buildRepo(loginFn = { _, _ -> defaultTokenDto })
            repo.saveTokens("stored_access", "stored_refresh")
            assertEquals("stored_access", repo.getAccessToken())
            assertEquals("stored_refresh", repo.getRefreshToken())
        }

    @Test
    fun `saveTokens overwrites previously stored tokens`() =
        runTest {
            val repo = buildRepo(loginFn = { _, _ -> defaultTokenDto })
            repo.saveTokens("first_access", "first_refresh")
            repo.saveTokens("second_access", "second_refresh")
            assertEquals("second_access", repo.getAccessToken())
            assertEquals("second_refresh", repo.getRefreshToken())
        }

    // ── clearTokens() ─────────────────────────────────────────────────────────

    @Test
    fun `clearTokens removes both stored tokens`() =
        runTest {
            val repo = buildRepo(loginFn = { _, _ -> defaultTokenDto })
            repo.saveTokens("access", "refresh")
            repo.clearTokens()
            assertNull(repo.getAccessToken())
            assertNull(repo.getRefreshToken())
        }

    @Test
    fun `clearTokens on empty storage does not throw`() =
        runTest {
            val repo = buildRepo(loginFn = { _, _ -> defaultTokenDto })
            repo.clearTokens()
            assertNull(repo.getAccessToken())
        }
}
