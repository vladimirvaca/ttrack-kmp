package com.rvladimir.ttrack.workoutprogress.presentation.components
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rvladimir.ttrack.ui.theme.BrandGreen
import com.rvladimir.ttrack.ui.theme.DarkBackground
import com.rvladimir.ttrack.ui.theme.TextGray

/**
 * Summary card shown when the workout session is complete.
 *
 * @param onFinish Called when the user taps "Done" to leave the screen.
 */
@Composable
internal fun DoneCard(onFinish: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Great work! 💪",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = DarkBackground,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "You've completed all your sets.",
            fontSize = 15.sp,
            color = TextGray,
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onFinish,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
        ) {
            Text(
                text = "Done",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkBackground,
            )
        }
    }
}
