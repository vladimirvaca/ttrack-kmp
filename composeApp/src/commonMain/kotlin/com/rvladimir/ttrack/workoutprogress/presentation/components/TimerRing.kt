package com.rvladimir.ttrack.workoutprogress.presentation.components
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rvladimir.ttrack.core.ui.extensions.toTimeString
import com.rvladimir.ttrack.ui.theme.DarkBackground
import com.rvladimir.ttrack.ui.theme.TextGray

/**
 * Circular timer ring displaying remaining time in the current phase.
 *
 * The arc starts full at the beginning of each phase and depletes to zero
 * as the phase elapses, then resets when the next phase begins.
 *
 * @param animatedPhaseProgress Animated fraction [0f, 1f] — 1f = phase just started.
 * @param phaseColor Ring accent colour for the current phase.
 * @param secondsLeft Remaining seconds shown in the centre label.
 * @param totalGoalSeconds Total workout duration shown as a sub-label.
 */
@Composable
internal fun TimerRing(
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
            drawArc(
                color = Color(0xFFE5E7EB),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidthBg, cap = StrokeCap.Round),
            )
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
                Text(text = "⏱", fontSize = 14.sp)
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
