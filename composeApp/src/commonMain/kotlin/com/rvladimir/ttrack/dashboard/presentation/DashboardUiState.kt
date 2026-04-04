package com.rvladimir.ttrack.dashboard.presentation

/**
 * UI state for the Dashboard screen.
 *
 * All three fields are derived locally — from the persisted session and the
 * device clock — so no network call is required on this screen.
 *
 * @property userName The authenticated user's first name.
 * @property greeting Time-sensitive salutation: "Good morning", "Good afternoon", or "Good evening".
 * @property formattedDate Human-readable current date, e.g. "Saturday, 4 Apr".
 */
data class DashboardUiState(
    val userName: String = "",
    val greeting: String = "",
    val formattedDate: String = "",
)
