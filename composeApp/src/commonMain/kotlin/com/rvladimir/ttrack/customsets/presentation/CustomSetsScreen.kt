package com.rvladimir.ttrack.customsets.presentation

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun CustomSetsScreen(onBack: () -> Unit = {}) {
    var prepTime by remember { mutableStateOf(10) }
    var workTime by remember { mutableStateOf(45) }
    var restTime by remember { mutableStateOf(15) }
    var rounds by remember { mutableStateOf(8) }

    val totalSeconds = (prepTime + (workTime + restTime) * rounds)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val timeDisplay = "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"

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
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                    ),
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

            // Total Time Circle
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

            // Configuration Cards
            DurationCard(
                title = "Preparation",
                seconds = prepTime,
                icon = Icons.Default.Restore,
                backgroundColor = PrepCardBg,
                iconColor = PrepIcon,
                onValueChange = { prepTime = (prepTime + it).coerceAtLeast(0) },
            )

            Spacer(modifier = Modifier.height(16.dp))

            DurationCard(
                title = "Work",
                seconds = workTime,
                icon = Icons.Default.FitnessCenter,
                backgroundColor = WorkCardBg,
                iconColor = WorkIcon,
                onValueChange = { workTime = (workTime + it).coerceAtLeast(1) },
            )

            Spacer(modifier = Modifier.height(16.dp))

            DurationCard(
                title = "Rest",
                seconds = restTime,
                icon = Icons.Default.Restore,
                backgroundColor = RestCardBg,
                iconColor = RestIcon,
                onValueChange = { restTime = (restTime + it).coerceAtLeast(0) },
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Rounds Card
            RoundsCard(
                rounds = rounds,
                onRoundsChange = { rounds = it },
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Start Button
            Button(
                onClick = { /* Start workout */ },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Start Workout",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBackground,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Add, // Using Add as a placeholder for the play-like icon in image
                        contentDescription = null,
                        tint = DarkBackground,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DurationCard(
    title: String,
    seconds: Int,
    icon: ImageVector,
    backgroundColor: Color,
    iconColor: Color,
    onValueChange: (Int) -> Unit,
) {
    val minutesStr = (seconds / 60).toString().padStart(2, '0')
    val secondsStr = (seconds % 60).toString().padStart(2, '0')
    val displayTime = "$minutesStr:$secondsStr"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier =
                        Modifier
                            .size(48.dp)
                            .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(24.dp),
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        color = TextGray,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = displayTime,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBackground,
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { onValueChange(-5) },
                    modifier =
                        Modifier
                            .size(32.dp)
                            .background(Color.White, CircleShape),
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease",
                        tint = DarkBackground,
                        modifier = Modifier.size(16.dp),
                    )
                }
                Spacer(modifier = Modifier.width(20.dp))
                IconButton(
                    onClick = { onValueChange(5) },
                    modifier =
                        Modifier
                            .size(32.dp)
                            .background(Color.White, CircleShape),
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase",
                        tint = DarkBackground,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun RoundsCard(
    rounds: Int,
    onRoundsChange: (Int) -> Unit,
) {
    // Keep a local string so the user can freely type; commit on valid integer
    var textValue by remember(rounds) { mutableStateOf(rounds.toString()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = LightGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Icon + label + editable value
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier =
                        Modifier
                            .size(48.dp)
                            .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Loop,
                        contentDescription = null,
                        tint = BrandGreen,
                        modifier = Modifier.size(24.dp),
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Rounds",
                        fontSize = 14.sp,
                        color = TextGray,
                        fontWeight = FontWeight.Medium,
                    )
                    BasicTextField(
                        value = textValue,
                        onValueChange = { input ->
                            // Allow only digit characters
                            val filtered = input.filter { it.isDigit() }
                            textValue = filtered
                            val parsed = filtered.toIntOrNull()
                            if (parsed != null && parsed >= 1) {
                                onRoundsChange(parsed)
                            }
                        },
                        textStyle =
                            TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkBackground,
                                textAlign = TextAlign.Start,
                            ),
                        cursorBrush = SolidColor(BrandGreen),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                    )
                }
            }

            // − / + buttons
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        val newVal = (rounds - 1).coerceAtLeast(1)
                        onRoundsChange(newVal)
                        textValue = newVal.toString()
                    },
                    modifier =
                        Modifier
                            .size(32.dp)
                            .background(Color.White, CircleShape),
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease rounds",
                        tint = DarkBackground,
                        modifier = Modifier.size(16.dp),
                    )
                }
                Spacer(modifier = Modifier.width(20.dp))
                IconButton(
                    onClick = {
                        val newVal = rounds + 1
                        onRoundsChange(newVal)
                        textValue = newVal.toString()
                    },
                    modifier =
                        Modifier
                            .size(32.dp)
                            .background(Color.White, CircleShape),
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase rounds",
                        tint = DarkBackground,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}
