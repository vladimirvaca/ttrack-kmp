package com.rvladimir.ttrack.registration

import com.rvladimir.ttrack.registration.domain.repository.RegisterRepository
import com.rvladimir.ttrack.registration.domain.usecase.RegisterUseCase
import com.rvladimir.ttrack.registration.presentation.RegisterUiState
import com.rvladimir.ttrack.registration.presentation.RegisterViewModel
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
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {
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
            ): Result<Unit> = Result.failure(RuntimeException("Server error"))
        }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun viewModelWith(repository: RegisterRepository) = RegisterViewModel(RegisterUseCase(repository))

    private fun RegisterViewModel.registerValid() =
        register(
            firstName = "Tony",
            lastName = "Stark",
            nickname = "IronMan",
            dateBirth = "1991-01-01",
            email = "tony@example.com",
            password = "12345",
        )

    // ── Initial state ─────────────────────────────────────────────────────────

    @Test
    fun `initial state is Idle`() =
        runTest {
            val vm = viewModelWith(successRepository)
            assertIs<RegisterUiState.Idle>(vm.uiState.value)
        }

    // ── Loading ───────────────────────────────────────────────────────────────

    @Test
    fun `state is Loading while registration is in progress`() =
        runTest {
            val latch = CompletableDeferred<Unit>()
            val hangingRepository =
                object : RegisterRepository {
                    override suspend fun register(
                        firstName: String,
                        lastName: String,
                        nickname: String,
                        dateBirth: String,
                        email: String,
                        password: String,
                    ): Result<Unit> {
                        latch.await() // suspends until explicitly completed
                        return Result.success(Unit)
                    }
                }
            val vm = viewModelWith(hangingRepository)
            vm.registerValid()
            // Advance past the launch { _uiState.value = Loading } point
            advanceUntilIdle()
            assertIs<RegisterUiState.Loading>(vm.uiState.value)
            // Unblock so the coroutine can clean up
            latch.complete(Unit)
            advanceUntilIdle()
        }

    // ── Success ───────────────────────────────────────────────────────────────

    @Test
    fun `state is Success after successful registration`() =
        runTest {
            val vm = viewModelWith(successRepository)
            vm.registerValid()
            advanceUntilIdle()
            assertIs<RegisterUiState.Success>(vm.uiState.value)
        }

    // ── Error from repository ─────────────────────────────────────────────────

    @Test
    fun `state is Error when repository fails`() =
        runTest {
            val vm = viewModelWith(failingRepository)
            vm.registerValid()
            advanceUntilIdle()
            assertIs<RegisterUiState.Error>(vm.uiState.value)
        }

    @Test
    fun `error message is propagated from repository`() =
        runTest {
            val vm = viewModelWith(failingRepository)
            vm.registerValid()
            advanceUntilIdle()
            val state = vm.uiState.value as RegisterUiState.Error
            kotlin.test.assertEquals("Server error", state.message)
        }

    // ── Validation errors ─────────────────────────────────────────────────────

    @Test
    fun `state is Error immediately when firstName is blank`() =
        runTest {
            val vm = viewModelWith(successRepository)
            vm.register(
                firstName = "",
                lastName = "Stark",
                nickname = "IronMan",
                dateBirth = "1991-01-01",
                email = "tony@example.com",
                password = "12345",
            )
            advanceUntilIdle()
            assertIs<RegisterUiState.Error>(vm.uiState.value)
        }

    @Test
    fun `state is Error when email is blank`() =
        runTest {
            val vm = viewModelWith(successRepository)
            vm.register(
                firstName = "Tony",
                lastName = "Stark",
                nickname = "IronMan",
                dateBirth = "1991-01-01",
                email = "  ",
                password = "12345",
            )
            advanceUntilIdle()
            assertIs<RegisterUiState.Error>(vm.uiState.value)
        }

    @Test
    fun `state is Error when dateBirth is blank`() =
        runTest {
            val vm = viewModelWith(successRepository)
            vm.register(
                firstName = "Tony",
                lastName = "Stark",
                nickname = "IronMan",
                dateBirth = "",
                email = "tony@example.com",
                password = "12345",
            )
            advanceUntilIdle()
            assertIs<RegisterUiState.Error>(vm.uiState.value)
        }

    // ── resetState ────────────────────────────────────────────────────────────

    @Test
    fun `resetState transitions back to Idle from Error`() =
        runTest {
            val vm = viewModelWith(failingRepository)
            vm.registerValid()
            advanceUntilIdle()
            assertIs<RegisterUiState.Error>(vm.uiState.value)
            vm.resetState()
            assertIs<RegisterUiState.Idle>(vm.uiState.value)
        }

    @Test
    fun `resetState transitions back to Idle from Success`() =
        runTest {
            val vm = viewModelWith(successRepository)
            vm.registerValid()
            advanceUntilIdle()
            assertIs<RegisterUiState.Success>(vm.uiState.value)
            vm.resetState()
            assertIs<RegisterUiState.Idle>(vm.uiState.value)
        }
}
