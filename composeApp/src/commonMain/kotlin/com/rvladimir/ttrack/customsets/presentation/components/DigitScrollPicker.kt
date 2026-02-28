package com.rvladimir.ttrack.customsets.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rvladimir.ttrack.ui.theme.BrandGreen
import com.rvladimir.ttrack.ui.theme.DarkBackground
import com.rvladimir.ttrack.ui.theme.TextGray

/**
 * A vertically scrollable digit picker with infinite circular wrapping and snapping.
 *
 * @param selected The currently selected digit value.
 * @param maxValue The inclusive maximum value (e.g. 9, 59, 99). Min is always 0.
 * @param onSelectedChange Callback invoked whenever the selected digit changes.
 */
@Composable
fun DigitScrollPicker(
    selected: Int,
    maxValue: Int,
    onSelectedChange: (Int) -> Unit,
) {
    val itemHeightDp = 52.dp
    val range = maxValue + 1 // total distinct values (0..maxValue)

    // Large virtual list to simulate infinite scroll; start near the centre aligned to `selected`
    val totalItems = range * 1_000
    val startIndex = (totalItems / 2) - ((totalItems / 2) % range) + selected

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = startIndex)
    val snapBehavior = rememberSnapFlingBehavior(listState)

    val derivedSelected by remember {
        derivedStateOf { listState.firstVisibleItemIndex % range }
    }

    LaunchedEffect(derivedSelected) {
        onSelectedChange(derivedSelected)
    }

    Box(
        modifier =
            Modifier
                .width(72.dp)
                .height(itemHeightDp * 5),
    ) {
        LazyColumn(
            state = listState,
            flingBehavior = snapBehavior,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = itemHeightDp * 2),
        ) {
            items(totalItems) { index ->
                val digit = index % range
                val rawDistance = kotlin.math.abs(derivedSelected - digit)
                val distance = minOf(rawDistance, range - rawDistance)
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
                            .width(72.dp)
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

        // Highlight bar behind the selected item
        Box(
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .width(72.dp)
                    .height(itemHeightDp)
                    .background(BrandGreen.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
        )
    }
}
