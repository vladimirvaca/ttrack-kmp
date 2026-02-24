package com.rvladimir.ttrack.auth.presentation

/**
 * Represents all possible UI states for the login screen.
 */
sealed interface LoginUiState {
    /** Initial idle state — form is editable, no action in progress. */
    data object Idle : LoginUiState

    /** A login request is in flight. */
    data object Loading : LoginUiState

    /**
     * Login succeeded.
     *
     * @property userId The authenticated user's identifier.
     */
    data class Success(
        val userId: String,
    ) : LoginUiState

    /**
     * Login failed.
     *
     * @property message A human-readable error message.
     */
    data class Error(
        val message: String,
    ) : LoginUiState
}
