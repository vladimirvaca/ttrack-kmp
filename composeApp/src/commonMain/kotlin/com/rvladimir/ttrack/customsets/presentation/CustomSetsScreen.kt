package com.rvladimir.ttrack.customsets.presentation

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rvladimir.ttrack.customsets.presentation.components.DurationCard
import com.rvladimir.ttrack.customsets.presentation.components.DurationPickerBottomSheet
import com.rvladimir.ttrack.customsets.presentation.components.RoundsCard
import com.rvladimir.ttrack.customsets.presentation.components.RoundsPickerBottomSheet
import com.rvladimir.ttrack.ui.theme.BrandGreen
import com.rvladimir.ttrack.ui.theme.DarkBackground
import com.rvladimir.ttrack.ui.theme.LightGray
import com.rvladimir.ttrack.ui.theme.PrepCardBg
import com.rvladimir.ttrack.ui.theme.PrepIcon
import com.rvladimir.ttrack.ui.theme.RestCardBg
import com.rvladimir.ttrack.ui.theme.RestIcon
import com.rvladimir.ttrack.ui.theme.TextGray
import com.rvladimir.ttrack.ui.theme.WorkCardBg
import com.rvladimir.ttrack.ui.theme.WorkIcon

/** Which duration card is currently showing its picker sheet, if any. */
private enum class DurationPicker { PREP, WORK, REST, NONE }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun CustomSetsScreen(onBack: () -> Unit = {}) {
    var prepTime by remember { mutableStateOf(10) }
    var workTime by remember { mutableStateOf(45) }
    var restTime by remember { mutableStateOf(15) }
    var rounds by remember { mutableStateOf(8) }

    var activeDurationPicker by remember { mutableStateOf(DurationPicker.NONE) }
    var showRoundsPicker by remember { mutableStateOf(false) }

    val totalSeconds = prepTime + (workTime + restTime) * rounds
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val timeDisplay =
        "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Configure Routine",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back",
                            tint = Color.Black,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            )
        },
        containerColor = Color.White,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Total Time Ring
            Box(
                modifier = Modifier.size(180.dp),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.size(160.dp)) {
                    drawArc(
                        color = LightGray.copy(alpha = 0.5f),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round),
                    )
                    drawArc(
                        color = BrandGreen,
                        startAngle = 135f,
                        sweepAngle = 270f,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round),
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "TOTAL TIME",
                        fontSize = 12.sp,
                        color = TextGray,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = timeDisplay,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBackground,
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            DurationCard(
                title = "Preparation",
                seconds = prepTime,
                icon = Icons.Default.Restore,
                backgroundColor = PrepCardBg,
                iconColor = PrepIcon,
                onClick = { activeDurationPicker = DurationPicker.PREP },
            )

            Spacer(modifier = Modifier.height(16.dp))

            DurationCard(
                title = "Work",
                seconds = workTime,
                icon = Icons.Default.FitnessCenter,
                backgroundColor = WorkCardBg,
                iconColor = WorkIcon,
                onClick = { activeDurationPicker = DurationPicker.WORK },
            )

            Spacer(modifier = Modifier.height(16.dp))

            DurationCard(
                title = "Rest",
                seconds = restTime,
                icon = Icons.Default.Restore,
                backgroundColor = RestCardBg,
                iconColor = RestIcon,
                onClick = { activeDurationPicker = DurationPicker.REST },
            )

            Spacer(modifier = Modifier.height(16.dp))

            RoundsCard(
                rounds = rounds,
                onClick = { showRoundsPicker = true },
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { /* Start workout */ },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "Start Workout",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBackground,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = DarkBackground,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Duration pickers
    if (activeDurationPicker == DurationPicker.PREP) {
        DurationPickerBottomSheet(
            title = "Preparation Time",
            currentSeconds = prepTime,
            onDismiss = { activeDurationPicker = DurationPicker.NONE },
            onConfirm = { secs ->
                prepTime = secs
                activeDurationPicker = DurationPicker.NONE
            },
        )
    }

    if (activeDurationPicker == DurationPicker.WORK) {
        DurationPickerBottomSheet(
            title = "Work Time",
            currentSeconds = workTime,
            onDismiss = { activeDurationPicker = DurationPicker.NONE },
            onConfirm = { secs ->
                workTime = secs.coerceAtLeast(1)
                activeDurationPicker = DurationPicker.NONE
            },
        )
    }

    if (activeDurationPicker == DurationPicker.REST) {
        DurationPickerBottomSheet(
            title = "Rest Time",
            currentSeconds = restTime,
            onDismiss = { activeDurationPicker = DurationPicker.NONE },
            onConfirm = { secs ->
                restTime = secs
                activeDurationPicker = DurationPicker.NONE
            },
        )
    }

    // Rounds picker
    if (showRoundsPicker) {
        RoundsPickerBottomSheet(
            currentRounds = rounds,
            onDismiss = { showRoundsPicker = false },
            onConfirm = { selected ->
                rounds = selected
                showRoundsPicker = false
            },
        )
    }
}
