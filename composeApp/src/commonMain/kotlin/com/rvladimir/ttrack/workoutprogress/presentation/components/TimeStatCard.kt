package com.rvladimir.ttrack.workoutprogress.presentation.components
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rvladimir.ttrack.ui.theme.DarkBackground
import com.rvladimir.ttrack.ui.theme.LightGray
import com.rvladimir.ttrack.ui.theme.TextGray

/**
 * A compact stat card displaying a [label] above a large [value].
 * Used to show elapsed and remaining times side-by-side.
 *
 * @param label Short upper-case label (e.g. "ELAPSED").
 * @param value Formatted value string (e.g. "02:30").
 * @param modifier Optional outer modifier.
 */
@Composable
internal fun TimeStatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            modifier
                .background(color = LightGray, shape = RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = TextGray,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = DarkBackground,
        )
    }
}
