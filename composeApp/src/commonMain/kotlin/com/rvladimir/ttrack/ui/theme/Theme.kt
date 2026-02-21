package com.rvladimir.ttrack.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme =
    lightColorScheme(
        primary = BrandGreen,
        onPrimary = Color.Black,
        surface = Color.White,
        onSurface = Color.Black,
        background = Color.White,
        onBackground = Color.Black,
    )

@Composable
fun TTrackTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content,
    )
}
