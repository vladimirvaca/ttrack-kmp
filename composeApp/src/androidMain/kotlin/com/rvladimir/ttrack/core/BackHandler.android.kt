package com.rvladimir.ttrack.core

import androidx.compose.runtime.Composable

/**
 * Android actual implementation — delegates to [androidx.activity.compose.BackHandler]
 * so the system back button is intercepted when [enabled] is `true`.
 */
@Composable
actual fun BackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
) {
    androidx.activity.compose.BackHandler(enabled = enabled, onBack = onBack)
}
