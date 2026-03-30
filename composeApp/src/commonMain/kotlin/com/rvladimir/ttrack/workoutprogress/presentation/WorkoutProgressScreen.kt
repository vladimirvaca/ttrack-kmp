package com.rvladimir.ttrack.workoutprogress.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rvladimir.ttrack.core.BackHandler
import com.rvladimir.ttrack.core.ui.extensions.toTimeString
import com.rvladimir.ttrack.ui.theme.BrandGreen
import com.rvladimir.ttrack.ui.theme.DarkBackground
import com.rvladimir.ttrack.workoutprogress.domain.model.WorkoutUiState
import com.rvladimir.ttrack.workoutprogress.presentation.components.CancelWorkoutDialog
import com.rvladimir.ttrack.workoutprogress.presentation.components.DoneCard
import com.rvladimir.ttrack.workoutprogress.presentation.components.NextPhaseBanner
import com.rvladimir.ttrack.workoutprogress.presentation.components.RoutineProgressBar
import com.rvladimir.ttrack.workoutprogress.presentation.components.TimeStatCard
import com.rvladimir.ttrack.workoutprogress.presentation.components.TimerRing

/**
 * Entry-point composable for the Workout Progress screen.
 *
 * Creates a [WorkoutProgressViewModel] scoped to this destination and delegates
 * rendering to the stateless [WorkoutProgressContent].
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
    var wasPausedBeforeDialog by remember { mutableStateOf(false) }

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
                if (!wasPausedBeforeDialog && uiState.isPaused) onPause()
            },
        )
    }

    val phaseColor by animateColorAsState(
        targetValue = uiState.currentPhase.displayColor(),
        animationSpec = tween(durationMillis = 600),
        label = "phaseColor",
    )

    val overallProgressFraction =
        when {
            uiState.isDone -> {
                1f
            }

            uiState.totalSeconds > 0 -> {
                (uiState.totalElapsed.toFloat() / uiState.totalSeconds.toFloat()).coerceIn(0f, 1f)
            }

            else -> {
                1f
            }
        }

    val animatedOverallProgress by animateFloatAsState(
        targetValue = overallProgressFraction,
        animationSpec = tween(durationMillis = 800),
        label = "overallProgress",
    )

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
                    value = (uiState.totalSeconds - uiState.totalElapsed).coerceAtLeast(0).toTimeString(),
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
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        border =
                            androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                DarkBackground.copy(alpha = 0.15f),
                            ),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkBackground),
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Skip",
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Skip", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = onTogglePause,
                        modifier = Modifier.weight(2f).height(52.dp),
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

// ─── Preview ─────────────────────────────────────────────────────────────────

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
