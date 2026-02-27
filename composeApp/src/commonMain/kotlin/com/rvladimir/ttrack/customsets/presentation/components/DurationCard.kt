package com.rvladimir.ttrack.customsets.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rvladimir.ttrack.ui.theme.DarkBackground
import com.rvladimir.ttrack.ui.theme.TextGray

/**
 * Displays a duration card showing [title] and a formatted [seconds] value (MM:SS).
 * Tapping the card triggers [onClick] to open the duration picker.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DurationCard(
    title: String,
    seconds: Int,
    icon: ImageVector,
    backgroundColor: Color,
    iconColor: Color,
    onClick: () -> Unit,
) {
    val minutesStr = (seconds / 60).toString().padStart(2, '0')
    val secondsStr = (seconds % 60).toString().padStart(2, '0')
    val displayTime = "$minutesStr:$secondsStr"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = onClick,
    ) {
        Box(
            modifier =
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
        ) {
            // Icon pinned to the left
            Box(
                modifier =
                    Modifier
                        .size(48.dp)
                        .background(Color.White, CircleShape)
                        .align(Alignment.CenterStart),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp),
                )
            }

            // Title + time centered over the full card width
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
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
    }
}
