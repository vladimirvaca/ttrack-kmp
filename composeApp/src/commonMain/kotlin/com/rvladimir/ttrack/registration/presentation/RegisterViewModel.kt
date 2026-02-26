package com.rvladimir.ttrack.registration.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the [RegisterScreen].
 * Follows unidirectional data flow: UI events → [register] → [uiState].
 *
 * Backend wiring is deferred — the use case will be injected once the
 * registration endpoint is connected.
 */
class RegisterViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)

    /** Observable UI state consumed by [RegisterScreen]. */
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    /**
     * Triggers a registration request with the provided details.
     *
     * @param firstName The user's first name.
     * @param lastName The user's last name.
     * @param nickname An optional display nickname.
     * @param email The user's email address.
     * @param password The user's chosen password.
     */
    fun register(
        firstName: String,
        lastName: String,
        nickname: String,
        email: String,
        password: String,
    ) {
        if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = RegisterUiState.Error("Please fill in all required fields.")
            return
        }
        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading
            // TODO: invoke RegisterUseCase once the backend is wired.
            _uiState.value = RegisterUiState.Error("Registration not yet implemented.")
        }
    }

    /** Resets the state back to [RegisterUiState.Idle] (e.g. after an error is shown). */
    fun resetState() {
        _uiState.value = RegisterUiState.Idle
    }
}
