package com.rvladimir.ttrack.workoutprogress.presentation

import androidx.compose.ui.graphics.Color
import com.rvladimir.ttrack.ui.theme.BrandGreen
import com.rvladimir.ttrack.ui.theme.PrepIcon
import com.rvladimir.ttrack.ui.theme.RestIcon
import com.rvladimir.ttrack.ui.theme.WorkIcon
import com.rvladimir.ttrack.workoutprogress.domain.model.WorkoutPhase

/** Returns the human-readable label for each [WorkoutPhase]. */
internal fun WorkoutPhase.displayLabel(): String =
    when (this) {
        WorkoutPhase.PREP -> "Preparation"
        WorkoutPhase.WORK -> "Work"
        WorkoutPhase.REST -> "Rest"
        WorkoutPhase.DONE -> "Done"
    }

/**
 * Returns the brand colour associated with each [WorkoutPhase].
 *
 * These colours drive the ring, badge, and banner tints throughout
 * the workout progress screen.
 */
internal fun WorkoutPhase.displayColor(): Color =
    when (this) {
        WorkoutPhase.PREP -> PrepIcon
        WorkoutPhase.WORK -> WorkIcon
        WorkoutPhase.REST -> RestIcon
        WorkoutPhase.DONE -> BrandGreen
    }
