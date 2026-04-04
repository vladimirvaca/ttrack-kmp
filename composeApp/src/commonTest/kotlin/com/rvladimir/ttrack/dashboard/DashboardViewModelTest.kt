package com.rvladimir.ttrack.dashboard

import com.rvladimir.ttrack.auth.domain.model.AuthResult
import com.rvladimir.ttrack.auth.domain.model.UserSession
import com.rvladimir.ttrack.auth.domain.repository.AuthRepository
import com.rvladimir.ttrack.auth.domain.usecase.GetUserProfileUseCase
import com.rvladimir.ttrack.dashboard.presentation.DashboardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.TimeZone
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Unit tests for [DashboardViewModel].
 *
 * A [FakeClock] and [TimeZone.UTC] are injected so that greeting and date
 * assertions are deterministic and independent of the host machine's wall-clock
 * or local timezone.
 *
 * All instants are pinned to 2026-04-04 (a Saturday) at various UTC hours so
 * that date-format expectations are predictable.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── Fakes ─────────────────────────────────────────────────────────────────

    /** Returns an [Instant] at [hour]:00:00 UTC on 2026-04-04 (a Saturday). */
    private fun instantAtHour(hour: Int): Instant =
        Instant.parse("2026-04-04T${hour.toString().padStart(2, '0')}:00:00Z")

    private class FakeClock(
        private val fixedInstant: Instant,
    ) : Clock {
        override fun now(): Instant = fixedInstant
    }

    /**
     * Returns a fake [AuthRepository] whose session fields are fully configurable.
     * Any field set to `null` simulates a missing/incomplete session.
     */
    private fun fakeRepository(
        accessToken: String? = "access_tok",
        refreshToken: String? = "refresh_tok",
        userId: Long? = 1L,
        name: String? = "John",
        lastName: String? = "Doe",
        email: String? = "john@example.com",
    ): AuthRepository =
        object : AuthRepository {
            override suspend fun login(
                email: String,
                password: String,
            ): Result<UserSession> = Result.failure(NotImplementedError("not used in dashboard tests"))

            override suspend fun refreshToken(refreshToken: String): Result<AuthResult> =
                Result.failure(NotImplementedError("not used in dashboard tests"))

            override fun saveSession(session: UserSession) = Unit

            override fun saveTokens(
                accessToken: String,
                refreshToken: String,
            ) = Unit

            override fun getAccessToken(): String? = accessToken

            override fun getRefreshToken(): String? = refreshToken

            override fun getUserId(): Long? = userId

            override fun getUserName(): String? = name

            override fun getUserLastName(): String? = lastName

            override fun getUserEmail(): String? = email

            override fun clearTokens() = Unit
        }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun vmAt(
        hour: Int,
        name: String? = "John",
        accessToken: String? = "access_tok",
        refreshToken: String? = "refresh_tok",
    ) = DashboardViewModel(
        getUserProfileUseCase =
            GetUserProfileUseCase(
                fakeRepository(
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    name = name,
                ),
            ),
        clock = FakeClock(instantAtHour(hour)),
        timeZone = TimeZone.UTC,
    )

    // ── userName ──────────────────────────────────────────────────────────────

    @Test
    fun `userName is populated from the stored session`() {
        assertEquals("Alice", vmAt(hour = 9, name = "Alice").uiState.value.userName)
    }

    @Test
    fun `userName is empty string when no access token is stored`() {
        assertEquals("", vmAt(hour = 9, accessToken = null).uiState.value.userName)
    }

    @Test
    fun `userName is empty string when no refresh token is stored`() {
        assertEquals("", vmAt(hour = 9, refreshToken = null).uiState.value.userName)
    }

    @Test
    fun `userName is empty string when name field is absent`() {
        assertEquals("", vmAt(hour = 9, name = null).uiState.value.userName)
    }

    // ── greeting — morning (hours 5–11) ───────────────────────────────────────

    @Test
    fun `greeting is Good morning at lower boundary hour 5`() {
        assertEquals("Good morning", vmAt(5).uiState.value.greeting)
    }

    @Test
    fun `greeting is Good morning at mid-morning hour 9`() {
        assertEquals("Good morning", vmAt(9).uiState.value.greeting)
    }

    @Test
    fun `greeting is Good morning at upper boundary hour 11`() {
        assertEquals("Good morning", vmAt(11).uiState.value.greeting)
    }

    // ── greeting — afternoon (hours 12–17) ────────────────────────────────────

    @Test
    fun `greeting is Good afternoon at lower boundary hour 12`() {
        assertEquals("Good afternoon", vmAt(12).uiState.value.greeting)
    }

    @Test
    fun `greeting is Good afternoon at mid-afternoon hour 14`() {
        assertEquals("Good afternoon", vmAt(14).uiState.value.greeting)
    }

    @Test
    fun `greeting is Good afternoon at upper boundary hour 17`() {
        assertEquals("Good afternoon", vmAt(17).uiState.value.greeting)
    }

    // ── greeting — evening (hours 18–23 and 0–4) ──────────────────────────────

    @Test
    fun `greeting is Good evening at lower boundary hour 18`() {
        assertEquals("Good evening", vmAt(18).uiState.value.greeting)
    }

    @Test
    fun `greeting is Good evening at late-night hour 23`() {
        assertEquals("Good evening", vmAt(23).uiState.value.greeting)
    }

    @Test
    fun `greeting is Good evening at midnight hour 0`() {
        assertEquals("Good evening", vmAt(0).uiState.value.greeting)
    }

    @Test
    fun `greeting is Good evening at pre-morning hour 4`() {
        assertEquals("Good evening", vmAt(4).uiState.value.greeting)
    }

    // ── formattedDate ─────────────────────────────────────────────────────────

    @Test
    fun `formattedDate contains the day of week name`() {
        // 2026-04-04 is a Saturday in UTC
        val date = vmAt(9).uiState.value.formattedDate
        assertTrue(date.startsWith("Saturday"), "Expected date to start with 'Saturday', got: $date")
    }

    @Test
    fun `formattedDate contains the numeric day of month`() {
        val date = vmAt(9).uiState.value.formattedDate
        assertTrue(date.contains("4"), "Expected date to contain day '4', got: $date")
    }

    @Test
    fun `formattedDate contains the three-letter month abbreviation`() {
        val date = vmAt(9).uiState.value.formattedDate
        assertTrue(date.contains("Apr"), "Expected date to contain 'Apr', got: $date")
    }

    @Test
    fun `formattedDate full format matches DayOfWeek comma day Month`() {
        // 2026-04-04 UTC → Saturday, day 4, April → "Saturday, 4 Apr"
        assertEquals("Saturday, 4 Apr", vmAt(9).uiState.value.formattedDate)
    }
}
