package com.rvladimir.ttrack.customsets.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun CustomSetsScreen(onBack: () -> Unit = {}) {
    var prepTime by remember { mutableStateOf(10) }
    var workTime by remember { mutableStateOf(45) }
    var restTime by remember { mutableStateOf(15) }
    var rounds by remember { mutableStateOf(8) }
    var showRoundsPicker by remember { mutableStateOf(false) }

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
                onClick = { showRoundsPicker = true },
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
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = DarkBackground,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

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
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = LightGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = onClick,
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
                    Text(
                        text = rounds.toString().padStart(2, '0'),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBackground,
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.Loop,
                contentDescription = "Pick rounds",
                tint = TextGray,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

/**
 * A bottom sheet that lets the user pick a number of rounds (01–99) by scrolling
 * two independent digit columns (tens on the left, units on the right).
 * A check button at the bottom confirms the selection.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundsPickerBottomSheet(
    currentRounds: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    val initialTens = (currentRounds / 10).coerceIn(0, 9)
    val initialUnits = (currentRounds % 10).coerceIn(0, 9)

    var selectedTens by remember { mutableStateOf(initialTens) }
    var selectedUnits by remember { mutableStateOf(initialUnits) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Select Rounds",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkBackground,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Scroll to set tens and units",
                fontSize = 13.sp,
                color = TextGray,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Digit pickers row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Tens column
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Tens",
                        fontSize = 12.sp,
                        color = TextGray,
                        fontWeight = FontWeight.Medium,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DigitScrollPicker(
                        selected = selectedTens,
                        onSelectedChange = { selectedTens = it },
                    )
                }

                Spacer(modifier = Modifier.width(48.dp))

                // Units column
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Units",
                        fontSize = 12.sp,
                        color = TextGray,
                        fontWeight = FontWeight.Medium,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DigitScrollPicker(
                        selected = selectedUnits,
                        onSelectedChange = { selectedUnits = it },
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Live preview of selected value
            Text(
                text = "$selectedTens$selectedUnits",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = DarkBackground,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Confirm button
            IconButton(
                onClick = {
                    val value = (selectedTens * 10 + selectedUnits).coerceAtLeast(1)
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        onConfirm(value)
                    }
                },
                modifier =
                    Modifier
                        .size(56.dp)
                        .background(BrandGreen, CircleShape),
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Confirm rounds",
                    tint = DarkBackground,
                    modifier = Modifier.size(28.dp),
                )
            }
        }
    }
}

/**
 * A vertically scrollable column showing digits 0–9 with infinite circular wrapping and snapping.
 * The selected (center) item is highlighted; neighbouring items fade out.
 * Scrolling past 9 wraps back to 0, and scrolling before 0 wraps to 9.
 */
@Composable
fun DigitScrollPicker(
    selected: Int,
    onSelectedChange: (Int) -> Unit,
) {
    val itemHeightDp = 52.dp
    val visibleItems = 5
    // Use a large total count to simulate infinite scroll; start in the middle
    val totalItems = 10_000
    val startIndex = (totalItems / 2) - ((totalItems / 2) % 10) + selected

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = startIndex)
    val snapBehavior = rememberSnapFlingBehavior(listState)

    // Map the current scroll index to the actual digit (0–9)
    val derivedSelected by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex % 10
        }
    }

    LaunchedEffect(derivedSelected) {
        onSelectedChange(derivedSelected)
    }

    Box(
        modifier =
            Modifier
                .width(64.dp)
                .height(itemHeightDp * visibleItems),
    ) {
        LazyColumn(
            state = listState,
            flingBehavior = snapBehavior,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            // Pad top/bottom so the first and last visible slots are half-visible
            contentPadding =
                PaddingValues(
                    vertical = itemHeightDp * 2,
                ),
        ) {
            items(totalItems) { index ->
                val digit = index % 10
                val rawDistance = kotlin.math.abs(derivedSelected - digit)
                val distance = minOf(rawDistance, 10 - rawDistance)
                val alpha =
                    when (distance) {
                        0 -> 1f
                        1 -> 0.5f
                        else -> 0.2f
                    }
                val fontSize = if (distance == 0) 32.sp else 22.sp
                Box(
                    modifier =
                        Modifier
                            .width(64.dp)
                            .height(itemHeightDp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = digit.toString(),
                        fontSize = fontSize,
                        fontWeight = if (distance == 0) FontWeight.Bold else FontWeight.Normal,
                        color = if (distance == 0) DarkBackground else TextGray,
                        modifier = Modifier.alpha(alpha),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        // Selection highlight bar drawn over the center slot
        Box(
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .height(itemHeightDp)
                    .background(BrandGreen.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
        )
    }
}
