package com.rvladimir.ttrack.core

import androidx.compose.runtime.Composable

/**
 * Multiplatform back-press handler.
 *
 * On Android this intercepts the system back button when [enabled] is `true`.
 * On iOS the hardware back button does not exist, so this is a no-op.
 *
 * @param enabled Whether the handler should be active.
 * @param onBack Lambda invoked when the back gesture / button is triggered.
 */
@Composable
expect fun BackHandler(
    enabled: Boolean = true,
    onBack: () -> Unit,
)
