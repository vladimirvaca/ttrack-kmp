package com.rvladimir.ttrack.core

import androidx.compose.runtime.Composable

/**
 * iOS actual implementation — no-op because iOS has no hardware back button.
 * The close (×) button in the top bar already handles cancellation.
 */
@Composable
actual fun BackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
) {
    // No hardware back button on iOS — intentionally empty.
}
