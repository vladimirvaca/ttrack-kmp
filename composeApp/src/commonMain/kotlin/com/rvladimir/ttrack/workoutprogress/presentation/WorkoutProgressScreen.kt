package com.rvladimir.ttrack.workoutprogress.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rvladimir.ttrack.core.BackHandler
import com.rvladimir.ttrack.ui.theme.BrandGreen
import com.rvladimir.ttrack.ui.theme.DarkBackground
import com.rvladimir.ttrack.ui.theme.LightGray
import com.rvladimir.ttrack.ui.theme.PrepIcon
import com.rvladimir.ttrack.ui.theme.RestIcon
import com.rvladimir.ttrack.ui.theme.TextGray
import com.rvladimir.ttrack.ui.theme.WorkIcon
import com.rvladimir.ttrack.workoutprogress.domain.model.WorkoutPhase
import com.rvladimir.ttrack.workoutprogress.domain.model.WorkoutUiState

/**
 * Entry-point composable for the Workout Progress screen.
 *
 * Creates a [WorkoutProgressViewModel] scoped to this destination and delegates
 * rendering to the stateless [WorkoutProgressContent].
 *
 * @param prepTime Preparation duration in seconds.
 * @param workTime Work interval duration in seconds.
 * @param restTime Rest interval duration in seconds.
 * @param rounds Number of work/rest cycles.
 * @param onFinish Callback invoked when the workout ends or the user cancels.
 */
@Composable
fun WorkoutProgressScreen(
    prepTime: Int,
    workTime: Int,
    restTime: Int,
    rounds: Int,
    onFinish: () -> Unit,
    onNavigateToDashboard: () -> Unit,
) {
    val workoutViewModel =
        viewModel {
            WorkoutProgressViewModel(
                prepTime = prepTime,
                workTime = workTime,
                restTime = restTime,
                rounds = rounds,
            )
        }
    val uiState by workoutViewModel.uiState.collectAsStateWithLifecycle()

    WorkoutProgressContent(
        uiState = uiState,
        onTogglePause = { workoutViewModel.togglePause() },
        onCancel = {
            workoutViewModel.cancel()
            onFinish()
        },
        onNavigateToDashboard = {
            workoutViewModel.cancel()
            onNavigateToDashboard()
        },
        onSkip = { workoutViewModel.skip() },
        onPause = { workoutViewModel.togglePause() },
    )
}

// ─── Stateless content ────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WorkoutProgressContent(
    uiState: WorkoutUiState,
    onTogglePause: () -> Unit,
    onCancel: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onSkip: () -> Unit = {},
    onPause: () -> Unit = {},
) {
    var showCancelDialog by remember { mutableStateOf(false) }
    // Track whether the session was already paused before the dialog appeared,
    // so we only resume if the user hadn't paused manually beforehand.
    var wasPausedBeforeDialog by remember { mutableStateOf(false) }

    // Intercept system back button — show confirmation instead of navigating immediately
    BackHandler(enabled = !uiState.isDone) {
        if (!showCancelDialog) {
            wasPausedBeforeDialog = uiState.isPaused
            if (!uiState.isPaused) onPause()
            showCancelDialog = true
        }
    }

    if (showCancelDialog) {
        CancelWorkoutDialog(
            onConfirm = {
                showCancelDialog = false
                onNavigateToDashboard()
            },
            onDismiss = {
                showCancelDialog = false
                // Only resume if the timer was running before the dialog opened
                if (!wasPausedBeforeDialog && uiState.isPaused) onPause()
            },
        )
    }

    val phaseColor by animateColorAsState(
        targetValue = uiState.currentPhase.displayColor(),
        animationSpec = tween(durationMillis = 600),
        label = "phaseColor",
    )

    // Overall progress — used only for the linear routine progress bar at the top.
    // When isDone we force 1f so the bar always reads exactly 100%.
    val overallProgressFraction =
        if (uiState.isDone) {
            1f
        } else if (uiState.totalSeconds > 0) {
            (uiState.totalElapsed.toFloat() / uiState.totalSeconds.toFloat()).coerceIn(0f, 1f)
        } else {
            1f
        }

    val animatedOverallProgress by animateFloatAsState(
        targetValue = overallProgressFraction,
        animationSpec = tween(durationMillis = 800),
        label = "overallProgress",
    )

    // Per-phase progress — drives the timer ring.
    // Starts at 1.0 (full circle) and shrinks to 0.0 as the phase elapses.
    val phaseProgressFraction =
        if (uiState.currentPhaseTotalSeconds > 0) {
            (uiState.currentPhaseSecondsLeft.toFloat() / uiState.currentPhaseTotalSeconds.toFloat())
                .coerceIn(0f, 1f)
        } else {
            0f
        }

    val animatedPhaseProgress by animateFloatAsState(
        targetValue = phaseProgressFraction,
        animationSpec = tween(durationMillis = 800),
        label = "phaseProgress",
    )

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Custom Sets",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = DarkBackground,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (uiState.isDone) {
                                onNavigateToDashboard()
                            } else if (!showCancelDialog) {
                                wasPausedBeforeDialog = uiState.isPaused
                                if (!uiState.isPaused) onPause()
                                showCancelDialog = true
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancel workout",
                                tint = DarkBackground,
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = DarkBackground,
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                )
                // ── Routine progress bar ────────────────────────────────────
                RoutineProgressBar(
                    progress = animatedOverallProgress,
                    progressPercent = (overallProgressFraction * 100).toInt(),
                    phaseColor = phaseColor,
                )
            }
        },
        containerColor = Color.White,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // ── Exercise / phase title ──────────────────────────────────────
            Text(
                text = if (uiState.isDone) "Workout Complete 🎉" else uiState.currentPhase.displayLabel(),
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DarkBackground,
            )
            Spacer(modifier = Modifier.height(8.dp))

            // ── Set badge ──────────────────────────────────────────────────
            if (!uiState.isDone) {
                Box(
                    modifier =
                        Modifier
                            .background(
                                color = phaseColor.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(20.dp),
                            ).padding(horizontal = 16.dp, vertical = 5.dp),
                ) {
                    Text(
                        text = "Set ${uiState.currentSet} of ${uiState.totalRounds}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = phaseColor,
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Timer ring ─────────────────────────────────────────────────
            TimerRing(
                animatedPhaseProgress = animatedPhaseProgress,
                phaseColor = phaseColor,
                secondsLeft = uiState.currentPhaseSecondsLeft,
                totalGoalSeconds = uiState.totalSeconds,
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ── Elapsed / Remaining stat cards ─────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                TimeStatCard(
                    modifier = Modifier.weight(1f),
                    label = "ELAPSED",
                    value = uiState.totalElapsed.toTimeString(),
                )
                TimeStatCard(
                    modifier = Modifier.weight(1f),
                    label = "REMAINING",
                    value =
                        (uiState.totalSeconds - uiState.totalElapsed)
                            .coerceAtLeast(0)
                            .toTimeString(),
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            if (uiState.isDone) {
                DoneCard(onFinish = onCancel)
                Spacer(modifier = Modifier.height(24.dp))
            } else {
                // ── Action buttons ─────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedButton(
                        onClick = onSkip,
                        modifier =
                            Modifier
                                .weight(1f)
                                .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        border =
                            androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                DarkBackground.copy(alpha = 0.15f),
                            ),
                        colors =
                            ButtonDefaults.outlinedButtonColors(
                                contentColor = DarkBackground,
                            ),
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Skip",
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Skip",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }

                    Button(
                        onClick = onTogglePause,
                        modifier =
                            Modifier
                                .weight(2f)
                                .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                    ) {
                        Icon(
                            imageVector = if (uiState.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = if (uiState.isPaused) "Resume" else "Pause",
                            tint = DarkBackground,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (uiState.isPaused) "Resume Session" else "Pause Session",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkBackground,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── Next phase / round banner ──────────────────────────────
                NextPhaseBanner(uiState = uiState, phaseColor = phaseColor)

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ─── Sub-composables ──────────────────────────────────────────────────────────

/**
 * Confirmation dialog shown when the user attempts to leave an active workout session.
 *
 * @param onConfirm Called when the user confirms they want to stop the workout.
 * @param onDismiss Called when the user chooses to continue the workout.
 */
@Composable
private fun CancelWorkoutDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Text(
                text = "Stop Training?",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = DarkBackground,
            )
        },
        text = {
            Text(
                text = "You're in the middle of your session. If you leave now, your progress won't be saved.",
                fontSize = 14.sp,
                color = TextGray,
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = DarkBackground,
                        contentColor = Color.White,
                    ),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(
                    text = "Stop Training",
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = BrandGreen,
                        contentColor = DarkBackground,
                    ),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(
                    text = "Keep Going!",
                    fontWeight = FontWeight.Bold,
                )
            }
        },
    )
}

/**
 * Linear progress bar at the top showing overall routine progress and round info.
 */
@Composable
private fun RoutineProgressBar(
    progress: Float,
    progressPercent: Int,
    phaseColor: Color,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Routine Progress",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextGray,
            )
            Text(
                text = "$progressPercent%",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkBackground,
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
            color = phaseColor,
            trackColor = LightGray,
            strokeCap = StrokeCap.Butt,
            gapSize = 0.dp,
            drawStopIndicator = {},
        )
        Spacer(modifier = Modifier.height(4.dp))
    }
}

/**
 * Circular timer ring displaying remaining time and goal.
 *
 * The arc represents the current phase only: full circle at phase start, shrinking to zero
 * as the phase elapses, then resets to full for the next phase.
 */
@Composable
private fun TimerRing(
    animatedPhaseProgress: Float,
    phaseColor: Color,
    secondsLeft: Int,
    totalGoalSeconds: Int,
) {
    Box(
        modifier = Modifier.size(220.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidthBg = 10.dp.toPx()
            val strokeWidthFg = 14.dp.toPx()
            val inset = strokeWidthFg / 2f
            val arcSize = Size(size.width - inset * 2, size.height - inset * 2)
            val topLeft = Offset(inset, inset)

            // Background full circle track
            drawArc(
                color = Color(0xFFE5E7EB),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidthBg, cap = StrokeCap.Round),
            )
            // Phase progress arc — full at start of phase, depletes to zero
            drawArc(
                color = phaseColor,
                startAngle = -90f,
                sweepAngle = 360f * animatedPhaseProgress,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidthFg, cap = StrokeCap.Round),
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "REMAINING",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextGray,
                letterSpacing = 1.5.sp,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = secondsLeft.toTimeString(),
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DarkBackground,
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = "Total time:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextGray,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "⏱",
                    fontSize = 14.sp,
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "${totalGoalSeconds.toTimeString()} mins",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextGray,
                )
            }
        }
    }
}

/**
 * A card displaying a time stat (label + value).
 */
@Composable
private fun TimeStatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            modifier
                .background(color = LightGray, shape = RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = TextGray,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = DarkBackground,
        )
    }
}

/**
 * Banner at the bottom showing what comes next in the workout.
 */
@Composable
private fun NextPhaseBanner(
    uiState: WorkoutUiState,
    phaseColor: Color,
) {
    val nextLabel =
        when (uiState.currentPhase) {
            WorkoutPhase.PREP -> {
                "Work interval begins next"
            }

            WorkoutPhase.WORK -> {
                if (uiState.currentSet >= uiState.totalRounds) {
                    "Last set — finish strong!"
                } else {
                    "Rest coming up next"
                }
            }

            WorkoutPhase.REST -> {
                "Work interval — Set ${uiState.currentSet + 1} of ${uiState.totalRounds}"
            }

            WorkoutPhase.DONE -> {
                ""
            }
        }

    if (nextLabel.isNotEmpty()) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(LightGray)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(40.dp)
                        .background(
                            color = phaseColor.copy(alpha = 0.15f),
                            shape = CircleShape,
                        ),
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

@Composable
private fun DoneCard(onFinish: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Great work! 💪",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = DarkBackground,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "You've completed all your sets.",
            fontSize = 15.sp,
            color = TextGray,
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onFinish,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
        ) {
            Text(
                text = "Done",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkBackground,
            )
        }
    }
}

// ─── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
fun WorkoutProgressScreenPreview() {
    WorkoutProgressScreen(
        prepTime = 5,
        workTime = 30,
        restTime = 10,
        rounds = 3,
        onFinish = {},
        onNavigateToDashboard = {},
    )
}

// ─── Extensions ───────────────────────────────────────────────────────────────

/** Formats seconds into MM:SS string representation. */
private fun Int.toTimeString(): String {
    val m = this / 60
    val s = this % 60
    return "${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
}

/** Returns the UI display label for each [WorkoutPhase]. */
private fun WorkoutPhase.displayLabel(): String =
    when (this) {
        WorkoutPhase.PREP -> "Preparation"
        WorkoutPhase.WORK -> "Work"
        WorkoutPhase.REST -> "Rest"
        WorkoutPhase.DONE -> "Done"
    }

/** Returns the brand color associated with each [WorkoutPhase]. */
private fun WorkoutPhase.displayColor(): Color =
    when (this) {
        WorkoutPhase.PREP -> PrepIcon
        WorkoutPhase.WORK -> WorkIcon
        WorkoutPhase.REST -> RestIcon
        WorkoutPhase.DONE -> BrandGreen
    }
