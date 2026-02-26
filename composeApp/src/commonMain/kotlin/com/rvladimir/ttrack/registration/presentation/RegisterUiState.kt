package com.rvladimir.ttrack.registration.presentation

/**
 * Represents all possible UI states for the Register screen.
 */
sealed interface RegisterUiState {
    /** Initial idle state — form is editable, no action in progress. */
    data object Idle : RegisterUiState

    /** A registration request is in flight. */
    data object Loading : RegisterUiState

    /** Registration succeeded. */
    data object Success : RegisterUiState

    /**
     * Registration failed.
     *
     * @property message A human-readable error message.
     */
    data class Error(
        val message: String,
    ) : RegisterUiState
}
