package com.rvladimir.ttrack.workoutprogress.presentation.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rvladimir.ttrack.ui.theme.BrandGreen
import com.rvladimir.ttrack.ui.theme.DarkBackground
import com.rvladimir.ttrack.ui.theme.TextGray

/**
 * Confirmation dialog shown when the user attempts to leave an active workout session.
 *
 * @param onConfirm Called when the user confirms they want to stop the workout.
 * @param onDismiss Called when the user chooses to continue the workout.
 */
@Composable
internal fun CancelWorkoutDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Text(
                text = "Stop Training?",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = DarkBackground,
            )
        },
        text = {
            Text(
                text = "You're in the middle of your session. If you leave now, your progress won't be saved.",
                fontSize = 14.sp,
                color = TextGray,
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = DarkBackground,
                        contentColor = Color.White,
                    ),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(text = "Stop Training", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = BrandGreen,
                        contentColor = DarkBackground,
                    ),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(text = "Keep Going!", fontWeight = FontWeight.Bold)
            }
        },
    )
}
