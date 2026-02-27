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
import androidx.compose.foundation.layout.width
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
 * A bottom sheet that lets the user pick a number of rounds (00–99) by scrolling
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
                    .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Select Rounds",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkBackground,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Scroll to set tens and units",
                fontSize = 13.sp,
                color = TextGray,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Tens column (0–9)
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
                        maxValue = 9,
                        onSelectedChange = { selectedTens = it },
                    )
                }

                Spacer(modifier = Modifier.width(48.dp))

                // Units column (0–9)
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
                        maxValue = 9,
                        onSelectedChange = { selectedUnits = it },
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Live preview
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
