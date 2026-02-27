package com.rvladimir.ttrack.customsets.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rvladimir.ttrack.ui.theme.BrandGreen
import com.rvladimir.ttrack.ui.theme.DarkBackground
import com.rvladimir.ttrack.ui.theme.TextGray
import kotlinx.coroutines.launch

/**
 * A bottom sheet that lets the user pick a duration by scrolling two digit columns:
 * - Left column: minutes (0–99)
 * - Right column: seconds (0–59)
 *
 * @param title The sheet title (e.g. "Work Time").
 * @param currentSeconds The current value in total seconds.
 * @param onDismiss Called when the sheet is dismissed without confirming.
 * @param onConfirm Called with the new total seconds value when the user confirms.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DurationPickerBottomSheet(
    title: String,
    currentSeconds: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    // Decompose currentSeconds into minutes (left, 0–99) and seconds (right, 0–59)
    val initialMinutes = (currentSeconds / 60).coerceIn(0, 99)
    val initialSecs = (currentSeconds % 60).coerceIn(0, 59)

    var selectedMinutes by remember { mutableStateOf(initialMinutes) }
    var selectedSecs by remember { mutableStateOf(initialSecs) }

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
                    .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkBackground,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Scroll to set minutes and seconds",
                fontSize = 13.sp,
                color = TextGray,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Column headers + pickers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Minutes (0–99)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Min",
                        fontSize = 12.sp,
                        color = TextGray,
                        fontWeight = FontWeight.Medium,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DigitScrollPicker(
                        selected = selectedMinutes,
                        maxValue = 99,
                        onSelectedChange = { selectedMinutes = it },
                    )
                }

                // Separator
                Text(
                    text = ":",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkBackground,
                    modifier = Modifier.padding(horizontal = 12.dp),
                )

                // Seconds (0–59)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Sec",
                        fontSize = 12.sp,
                        color = TextGray,
                        fontWeight = FontWeight.Medium,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DigitScrollPicker(
                        selected = selectedSecs,
                        maxValue = 59,
                        onSelectedChange = { selectedSecs = it },
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Live preview
            Text(
                text =
                    "${selectedMinutes.toString().padStart(2, '0')}:" +
                        selectedSecs.toString().padStart(2, '0'),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = DarkBackground,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Confirm button
            IconButton(
                onClick = {
                    val totalSecs = selectedMinutes * 60 + selectedSecs
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        onConfirm(totalSecs)
                    }
                },
                modifier =
                    Modifier
                        .size(56.dp)
                        .background(BrandGreen, CircleShape),
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Confirm duration",
                    tint = DarkBackground,
                    modifier = Modifier.size(28.dp),
                )
            }
        }
    }
}
