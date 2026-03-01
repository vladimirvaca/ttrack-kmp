package com.rvladimir.ttrack.workoutprogress.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rvladimir.ttrack.workoutprogress.domain.model.WorkoutPhase
import com.rvladimir.ttrack.workoutprogress.domain.model.WorkoutUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel that drives the workout timer state machine.
 *
 * Phase sequence: PREP → (WORK → REST) × [rounds] → DONE
 * REST is skipped after the last WORK interval.
 *
 * @param prepTime Preparation time in seconds.
 * @param workTime Work interval duration in seconds.
 * @param restTime Rest interval duration in seconds.
 * @param rounds Number of work/rest cycles to perform.
 */
class WorkoutProgressViewModel(
    private val prepTime: Int,
    private val workTime: Int,
    private val restTime: Int,
    private val rounds: Int,
) : ViewModel() {
    // REST is skipped after the last WORK set, so total = prep + work×rounds + rest×(rounds-1)
    private val totalSeconds: Int = prepTime + workTime * rounds + restTime * (rounds - 1).coerceAtLeast(0)

    private val _uiState =
        MutableStateFlow(
            WorkoutUiState(
                currentPhase = WorkoutPhase.PREP,
                currentPhaseSecondsLeft = prepTime,
                currentPhaseTotalSeconds = prepTime,
                currentSet = 1,
                totalRounds = rounds,
                totalElapsed = 0,
                totalSeconds = totalSeconds,
                isPaused = false,
                isDone = false,
            ),
        )

    /** Observable UI state consumed by WorkoutProgressScreen. */
    val uiState: StateFlow<WorkoutUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        startTicking()
    }

    private fun startTicking() {
        timerJob?.cancel()
        timerJob =
            viewModelScope.launch {
                while (true) {
                    delay(1_000L)
                    val current = _uiState.value
                    if (current.isPaused || current.isDone) continue
                    tick()
                }
            }
    }

    /**
     * Advances the timer by one second, transitioning phases as needed.
     * Exposed internally and for testing.
     */
    internal fun tick() {
        val current = _uiState.value
        if (current.isDone) return

        val newElapsed = current.totalElapsed + 1
        val newPhaseLeft = current.currentPhaseSecondsLeft - 1

        if (newPhaseLeft > 0) {
            // Still within the same phase
            _uiState.value =
                current.copy(
                    currentPhaseSecondsLeft = newPhaseLeft,
                    totalElapsed = newElapsed,
                )
            return
        }

        // Phase just ended — transition
        _uiState.value =
            when (current.currentPhase) {
                WorkoutPhase.PREP -> {
                    current.copy(
                        currentPhase = WorkoutPhase.WORK,
                        currentPhaseSecondsLeft = workTime,
                        currentPhaseTotalSeconds = workTime,
                        totalElapsed = newElapsed,
                    )
                }

                WorkoutPhase.WORK -> {
                    val isLastSet = current.currentSet >= rounds
                    if (isLastSet) {
                        // No rest after the last set — done; clamp elapsed to total for exact 100%
                        current.copy(
                            currentPhase = WorkoutPhase.DONE,
                            currentPhaseSecondsLeft = 0,
                            currentPhaseTotalSeconds = 0,
                            totalElapsed = totalSeconds,
                            isDone = true,
                        )
                    } else {
                        current.copy(
                            currentPhase = WorkoutPhase.REST,
                            currentPhaseSecondsLeft = restTime,
                            currentPhaseTotalSeconds = restTime,
                            totalElapsed = newElapsed,
                        )
                    }
                }

                WorkoutPhase.REST -> {
                    current.copy(
                        currentPhase = WorkoutPhase.WORK,
                        currentPhaseSecondsLeft = workTime,
                        currentPhaseTotalSeconds = workTime,
                        currentSet = current.currentSet + 1,
                        totalElapsed = newElapsed,
                    )
                }

                WorkoutPhase.DONE -> {
                    current
                }
            }
    }

    /** Toggles the pause/resume state of the timer. */
    fun togglePause() {
        _uiState.value = _uiState.value.copy(isPaused = !_uiState.value.isPaused)
    }

    /**
     * Skips the current phase immediately, advancing to the next phase as if the phase timer
     * had expired naturally.
     */
    fun skip() {
        val current = _uiState.value
        if (current.isDone) return
        val remaining = current.currentPhaseSecondsLeft
        repeat(remaining) { tick() }
    }

    /** Marks the workout as done (used when the user cancels or finishes early). */
    fun cancel() {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(isDone = true, isPaused = true)
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
