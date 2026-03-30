package com.rvladimir.ttrack.workoutprogress.presentation.components
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rvladimir.ttrack.ui.theme.DarkBackground
import com.rvladimir.ttrack.ui.theme.LightGray
import com.rvladimir.ttrack.ui.theme.TextGray

/**
 * Linear progress bar showing overall routine progress at the top of the workout screen.
 *
 * @param progress Animated progress fraction in [0f, 1f].
 * @param progressPercent Integer percentage shown as a label (0-100).
 * @param phaseColor Accent colour matching the current workout phase.
 */
@Composable
internal fun RoutineProgressBar(
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
