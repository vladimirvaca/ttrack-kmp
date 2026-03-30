package com.rvladimir.ttrack.workoutprogress

import com.rvladimir.ttrack.workoutprogress.domain.model.WorkoutPhase
import com.rvladimir.ttrack.workoutprogress.presentation.WorkoutProgressViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for [WorkoutProgressViewModel].
 *
 * The timer is driven by `tick()` calls directly (or via `advanceTimeBy`) so that
 * tests remain deterministic and millisecond-fast — no real wall-clock delays.
 *
 * Phase sequence under test: PREP → WORK → REST → WORK → ... → DONE
 */
@OptIn(ExperimentalCoroutinesApi::class)
class WorkoutProgressViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun vm(
        prepTime: Int = 5,
        workTime: Int = 10,
        restTime: Int = 5,
        rounds: Int = 2,
    ) = WorkoutProgressViewModel(
        prepTime = prepTime,
        workTime = workTime,
        restTime = restTime,
        rounds = rounds,
    )

    // ── Initial state ─────────────────────────────────────────────────────────

    @Test
    fun `initial phase is PREP`() {
        val state = vm().uiState.value
        assertEquals(WorkoutPhase.PREP, state.currentPhase)
    }

    @Test
    fun `initial secondsLeft equals prepTime`() {
        val state = vm(prepTime = 7).uiState.value
        assertEquals(7, state.currentPhaseSecondsLeft)
    }

    @Test
    fun `initial elapsed is zero`() {
        assertEquals(0, vm().uiState.value.totalElapsed)
    }

    @Test
    fun `initial isDone is false`() {
        assertFalse(vm().uiState.value.isDone)
    }

    @Test
    fun `initial isPaused is false`() {
        assertFalse(vm().uiState.value.isPaused)
    }

    @Test
    fun `totalSeconds is computed correctly`() {
        // prep=5 + work=10*2 + rest=5*(2-1) = 5+20+5 = 30
        assertEquals(30, vm(prepTime = 5, workTime = 10, restTime = 5, rounds = 2).uiState.value.totalSeconds)
    }

    // ── tick() — within a phase ───────────────────────────────────────────────

    @Test
    fun `tick decrements phase seconds left`() {
        val viewModel = vm(prepTime = 5)
        viewModel.tick()
        assertEquals(4, viewModel.uiState.value.currentPhaseSecondsLeft)
    }

    @Test
    fun `tick increments totalElapsed`() {
        val viewModel = vm()
        viewModel.tick()
        assertEquals(1, viewModel.uiState.value.totalElapsed)
    }

    @Test
    fun `phase does not change mid-phase`() {
        val viewModel = vm(prepTime = 5)
        repeat(4) { viewModel.tick() }
        assertEquals(WorkoutPhase.PREP, viewModel.uiState.value.currentPhase)
    }

    // ── tick() — phase transitions ────────────────────────────────────────────

    @Test
    fun `PREP transitions to WORK after prepTime ticks`() {
        val viewModel = vm(prepTime = 3, workTime = 10)
        repeat(3) { viewModel.tick() }
        assertEquals(WorkoutPhase.WORK, viewModel.uiState.value.currentPhase)
    }

    @Test
    fun `WORK transitions to REST after workTime ticks`() {
        val viewModel = vm(prepTime = 1, workTime = 3, restTime = 5, rounds = 2)
        repeat(1) { viewModel.tick() } // finish PREP
        repeat(3) { viewModel.tick() } // finish WORK set 1
        assertEquals(WorkoutPhase.REST, viewModel.uiState.value.currentPhase)
    }

    @Test
    fun `REST transitions back to WORK with incremented set`() {
        val viewModel = vm(prepTime = 1, workTime = 3, restTime = 2, rounds = 2)
        repeat(1) { viewModel.tick() } // PREP done
        repeat(3) { viewModel.tick() } // WORK set 1 done
        repeat(2) { viewModel.tick() } // REST done
        val state = viewModel.uiState.value
        assertEquals(WorkoutPhase.WORK, state.currentPhase)
        assertEquals(2, state.currentSet)
    }

    @Test
    fun `last WORK set transitions directly to DONE without REST`() {
        val viewModel = vm(prepTime = 1, workTime = 2, restTime = 99, rounds = 1)
        repeat(1) { viewModel.tick() } // PREP done
        repeat(2) { viewModel.tick() } // WORK done (single round — no rest)
        val state = viewModel.uiState.value
        assertEquals(WorkoutPhase.DONE, state.currentPhase)
        assertTrue(state.isDone)
    }

    @Test
    fun `totalElapsed equals totalSeconds when DONE`() {
        val viewModel = vm(prepTime = 1, workTime = 2, restTime = 99, rounds = 1)
        repeat(3) { viewModel.tick() }
        // total = 1 + 2*1 = 3 (rest skipped for last round)
        assertEquals(3, viewModel.uiState.value.totalElapsed)
        assertEquals(3, viewModel.uiState.value.totalSeconds)
    }

    @Test
    fun `full two-round session reaches DONE`() {
        // prep=2, work=3, rest=2, rounds=2
        // total = 2 + 3*2 + 2*(2-1) = 2+6+2 = 10
        val viewModel = vm(prepTime = 2, workTime = 3, restTime = 2, rounds = 2)
        repeat(10) { viewModel.tick() }
        val state = viewModel.uiState.value
        assertEquals(WorkoutPhase.DONE, state.currentPhase)
        assertTrue(state.isDone)
    }

    // ── togglePause() ─────────────────────────────────────────────────────────

    @Test
    fun `togglePause pauses a running timer`() {
        val viewModel = vm()
        viewModel.togglePause()
        assertTrue(viewModel.uiState.value.isPaused)
    }

    @Test
    fun `togglePause resumes a paused timer`() {
        val viewModel = vm()
        viewModel.togglePause()
        viewModel.togglePause()
        assertFalse(viewModel.uiState.value.isPaused)
    }

    @Test
    fun `ticking while paused does not advance the timer`() =
        runTest {
            val viewModel = vm(prepTime = 10)
            viewModel.togglePause()
            // Advance virtual time by 5 ticks — each tick is a no-op because isPaused == true
            advanceTimeBy(5_000L)
            assertEquals(10, viewModel.uiState.value.currentPhaseSecondsLeft)
            assertEquals(0, viewModel.uiState.value.totalElapsed)
            // Cancel the timer job before runTest's implicit advanceUntilIdle() runs,
            // otherwise the infinite while(true) loop would drain forever.
            viewModel.cancel()
        }

    // ── skip() ────────────────────────────────────────────────────────────────

    @Test
    fun `skip advances past PREP into WORK immediately`() {
        val viewModel = vm(prepTime = 30)
        viewModel.skip()
        assertEquals(WorkoutPhase.WORK, viewModel.uiState.value.currentPhase)
    }

    @Test
    fun `skip from WORK enters REST when not the last set`() {
        val viewModel = vm(prepTime = 1, workTime = 30, restTime = 5, rounds = 2)
        repeat(1) { viewModel.tick() } // finish PREP
        viewModel.skip() // skip WORK set 1
        assertEquals(WorkoutPhase.REST, viewModel.uiState.value.currentPhase)
    }

    @Test
    fun `skip from last WORK set marks workout as DONE`() {
        val viewModel = vm(prepTime = 1, workTime = 30, restTime = 5, rounds = 1)
        repeat(1) { viewModel.tick() } // finish PREP
        viewModel.skip() // skip the only WORK set
        assertTrue(viewModel.uiState.value.isDone)
    }

    // ── cancel() ─────────────────────────────────────────────────────────────

    @Test
    fun `cancel marks workout as done and paused`() {
        val viewModel = vm()
        viewModel.cancel()
        val state = viewModel.uiState.value
        assertTrue(state.isDone)
        assertTrue(state.isPaused)
    }

    @Test
    fun `tick after cancel is a no-op`() {
        val viewModel = vm(prepTime = 10)
        viewModel.cancel()
        val snapshot = viewModel.uiState.value
        viewModel.tick()
        assertEquals(snapshot, viewModel.uiState.value)
    }
}
