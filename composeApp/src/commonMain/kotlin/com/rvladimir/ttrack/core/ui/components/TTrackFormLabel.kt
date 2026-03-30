package com.rvladimir.ttrack.core.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rvladimir.ttrack.ui.theme.DarkBackground

/**
 * Shared form-field label composable used across login and registration screens.
 *
 * Renders [label] left-aligned in bold `14sp` using [DarkBackground] as text colour.
 *
 * @param label The label text to display.
 * @param modifier Optional outer modifier.
 */
@Composable
internal fun TTrackFormLabel(
    label: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = label,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = DarkBackground,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
        textAlign = TextAlign.Start,
    )
}
