package com.rvladimir.ttrack.auth

import com.rvladimir.ttrack.auth.domain.model.AuthResult
import com.rvladimir.ttrack.auth.domain.repository.AuthRepository
import com.rvladimir.ttrack.auth.domain.usecase.LoginUseCase
import com.rvladimir.ttrack.auth.presentation.LoginUiState
import com.rvladimir.ttrack.auth.presentation.LoginViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 * Unit tests for [LoginViewModel].
 *
 * [AuthRepository] is faked so that no network or platform storage is involved.
 * [Dispatchers.Main] is overridden with a [StandardTestDispatcher] to give
 * deterministic coroutine execution.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── Fake repositories ─────────────────────────────────────────────────────

    /**
     * A fake [AuthRepository] that immediately succeeds the login call and
     * records the last tokens saved via [saveTokens].
     */
    private class SuccessAuthRepository(
        private val accessToken: String = "access_tok_123",
        private val refreshToken: String = "refresh_tok_456",
    ) : AuthRepository {
        var savedAccessToken: String? = null
        var savedRefreshToken: String? = null
        var tokensCleared: Boolean = false

        override suspend fun login(
            email: String,
            password: String,
        ): Result<AuthResult> = Result.success(AuthResult(accessToken = accessToken, refreshToken = refreshToken))

        override suspend fun refreshToken(refreshToken: String): Result<AuthResult> =
            Result.success(AuthResult(accessToken = "new_access", refreshToken = "new_refresh"))

        override fun saveTokens(
            accessToken: String,
            refreshToken: String,
        ) {
            savedAccessToken = accessToken
            savedRefreshToken = refreshToken
        }

        override fun getAccessToken(): String? = savedAccessToken

        override fun getRefreshToken(): String? = savedRefreshToken

        override fun clearTokens() {
            tokensCleared = true
            savedAccessToken = null
            savedRefreshToken = null
        }
    }

    private val failingRepository =
        object : AuthRepository {
            override suspend fun login(
                email: String,
                password: String,
            ): Result<AuthResult> = Result.failure(RuntimeException("Invalid credentials"))

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

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun viewModelWith(repository: AuthRepository) = LoginViewModel(LoginUseCase(repository), repository)

    private fun LoginViewModel.loginValid() = login("user@example.com", "password123")

    // ── Initial state ─────────────────────────────────────────────────────────

    @Test
    fun `initial state is Idle`() =
        runTest {
            val vm = viewModelWith(SuccessAuthRepository())
            assertIs<LoginUiState.Idle>(vm.uiState.value)
        }

    // ── Loading ───────────────────────────────────────────────────────────────

    @Test
    fun `state is Loading while login is in progress`() =
        runTest {
            val latch = CompletableDeferred<Unit>()
            val hangingRepository =
                object : AuthRepository {
                    override suspend fun login(
                        email: String,
                        password: String,
                    ): Result<AuthResult> {
                        latch.await()
                        return Result.success(AuthResult("access", "refresh"))
                    }

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
            val vm = viewModelWith(hangingRepository)
            vm.loginValid()
            // Advance past the `_uiState.value = Loading` assignment
            advanceUntilIdle()
            assertIs<LoginUiState.Loading>(vm.uiState.value)
            latch.complete(Unit)
            advanceUntilIdle()
        }

    // ── Success ───────────────────────────────────────────────────────────────

    @Test
    fun `state is Success after successful login`() =
        runTest {
            val vm = viewModelWith(SuccessAuthRepository())
            vm.loginValid()
            advanceUntilIdle()
            assertIs<LoginUiState.Success>(vm.uiState.value)
        }

    @Test
    fun `tokens are persisted on successful login`() =
        runTest {
            val repo = SuccessAuthRepository(accessToken = "my_access", refreshToken = "my_refresh")
            val vm = viewModelWith(repo)
            vm.loginValid()
            advanceUntilIdle()
            assertEquals("my_access", repo.savedAccessToken)
            assertEquals("my_refresh", repo.savedRefreshToken)
        }

    // ── Error ─────────────────────────────────────────────────────────────────

    @Test
    fun `state is Error when login fails`() =
        runTest {
            val vm = viewModelWith(failingRepository)
            vm.loginValid()
            advanceUntilIdle()
            assertIs<LoginUiState.Error>(vm.uiState.value)
        }

    @Test
    fun `error message is propagated from the exception`() =
        runTest {
            val vm = viewModelWith(failingRepository)
            vm.loginValid()
            advanceUntilIdle()
            val state = vm.uiState.value as LoginUiState.Error
            assertEquals("Invalid credentials", state.message)
        }

    @Test
    fun `state is Error when email is blank`() =
        runTest {
            val vm = viewModelWith(SuccessAuthRepository())
            vm.login(email = "", password = "password123")
            advanceUntilIdle()
            assertIs<LoginUiState.Error>(vm.uiState.value)
        }

    @Test
    fun `state is Error when password is blank`() =
        runTest {
            val vm = viewModelWith(SuccessAuthRepository())
            vm.login(email = "user@example.com", password = "")
            advanceUntilIdle()
            assertIs<LoginUiState.Error>(vm.uiState.value)
        }

    @Test
    fun `error message fallback is used when exception has no message`() =
        runTest {
            val repoWithNullMessage =
                object : AuthRepository by failingRepository {
                    override suspend fun login(
                        email: String,
                        password: String,
                    ): Result<AuthResult> = Result.failure(RuntimeException()) // null message
                }
            val vm = viewModelWith(repoWithNullMessage)
            vm.loginValid()
            advanceUntilIdle()
            val state = vm.uiState.value as LoginUiState.Error
            assertEquals("An unexpected error occurred.", state.message)
        }

    // ── logout() ──────────────────────────────────────────────────────────────

    @Test
    fun `logout clears tokens and resets state to Idle`() =
        runTest {
            val repo = SuccessAuthRepository()
            val vm = viewModelWith(repo)
            vm.loginValid()
            advanceUntilIdle()
            assertIs<LoginUiState.Success>(vm.uiState.value)

            vm.logout()

            assertIs<LoginUiState.Idle>(vm.uiState.value)
            assertEquals(true, repo.tokensCleared)
        }

    @Test
    fun `logout from Idle state stays Idle and clears tokens`() =
        runTest {
            val repo = SuccessAuthRepository()
            val vm = viewModelWith(repo)
            vm.logout()
            assertIs<LoginUiState.Idle>(vm.uiState.value)
            assertEquals(true, repo.tokensCleared)
        }

    // ── resetState() ──────────────────────────────────────────────────────────

    @Test
    fun `resetState transitions back to Idle from Error`() =
        runTest {
            val vm = viewModelWith(failingRepository)
            vm.loginValid()
            advanceUntilIdle()
            assertIs<LoginUiState.Error>(vm.uiState.value)

            vm.resetState()

            assertIs<LoginUiState.Idle>(vm.uiState.value)
        }

    @Test
    fun `resetState transitions back to Idle from Success`() =
        runTest {
            val vm = viewModelWith(SuccessAuthRepository())
            vm.loginValid()
            advanceUntilIdle()
            assertIs<LoginUiState.Success>(vm.uiState.value)

            vm.resetState()

            assertIs<LoginUiState.Idle>(vm.uiState.value)
        }

    @Test
    fun `resetState from Idle stays Idle`() =
        runTest {
            val vm = viewModelWith(SuccessAuthRepository())
            vm.resetState()
            assertIs<LoginUiState.Idle>(vm.uiState.value)
        }
}
