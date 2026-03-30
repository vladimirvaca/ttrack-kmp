package com.rvladimir.ttrack.workoutprogress.presentation.components
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rvladimir.ttrack.ui.theme.DarkBackground
import com.rvladimir.ttrack.ui.theme.LightGray
import com.rvladimir.ttrack.ui.theme.TextGray
import com.rvladimir.ttrack.workoutprogress.domain.model.WorkoutPhase
import com.rvladimir.ttrack.workoutprogress.domain.model.WorkoutUiState

/**
 * Banner at the bottom of the workout screen showing what comes next in the session.
 *
 * Hidden when [WorkoutUiState.isDone] or after the last set.
 *
 * @param uiState Current workout state snapshot.
 * @param phaseColor Accent colour matching the current phase.
 */
@Composable
internal fun NextPhaseBanner(
    uiState: WorkoutUiState,
    phaseColor: Color,
) {
    val nextLabel =
        when (uiState.currentPhase) {
            WorkoutPhase.PREP -> "Work interval begins next"
            WorkoutPhase.WORK ->
                if (uiState.currentSet >= uiState.totalRounds) {
                    "Last set — finish strong!"
                } else {
                    "Rest coming up next"
                }
            WorkoutPhase.REST -> "Work interval — Set ${uiState.currentSet + 1} of ${uiState.totalRounds}"
            WorkoutPhase.DONE -> ""
        }
    if (nextLabel.isNotEmpty()) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(
                        androidx.compose.foundation.shape
                            .RoundedCornerShape(16.dp),
                    ).background(LightGray)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(40.dp)
                        .background(color = phaseColor.copy(alpha = 0.15f), shape = CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = null,
                    tint = phaseColor,
                    modifier = Modifier.size(22.dp),
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "NEXT",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextGray,
                    letterSpacing = 1.sp,
                )
                Text(
                    text = nextLabel,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkBackground,
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = TextGray,
            )
        }
    }
}
