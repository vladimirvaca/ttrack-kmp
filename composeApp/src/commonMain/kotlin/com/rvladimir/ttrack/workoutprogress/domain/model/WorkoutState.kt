package com.rvladimir.ttrack.workoutprogress.domain.model

/** Represents the current phase of a workout session. */
enum class WorkoutPhase {
    /** Countdown before the first work set begins. */
    PREP,

    /** Active work interval. */
    WORK,

    /** Rest interval between sets. */
    REST,

    /** All sets have been completed. */
    DONE,
}

/**
 * Immutable snapshot of the workout timer state exposed to the UI.
 *
 * @property currentPhase The phase currently running.
 * @property currentPhaseSecondsLeft Seconds remaining in [currentPhase].
 * @property currentPhaseTotalSeconds Total duration of [currentPhase] in seconds, used to
 *   compute per-phase ring progress.
 * @property currentSet The 1-based index of the set being executed (work/rest).
 * @property totalRounds Total number of work rounds configured.
 * @property totalElapsed Total elapsed seconds since the workout started.
 * @property totalSeconds Total duration of the full session in seconds.
 * @property isPaused Whether the timer is paused.
 * @property isDone Whether the workout has finished.
 */
data class WorkoutUiState(
    val currentPhase: WorkoutPhase = WorkoutPhase.PREP,
    val currentPhaseSecondsLeft: Int = 0,
    val currentPhaseTotalSeconds: Int = 0,
    val currentSet: Int = 1,
    val totalRounds: Int = 1,
    val totalElapsed: Int = 0,
    val totalSeconds: Int = 0,
    val isPaused: Boolean = false,
    val isDone: Boolean = false,
)
