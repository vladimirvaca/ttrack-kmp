package com.rvladimir.ttrack.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rvladimir.ttrack.auth.domain.usecase.LoginUseCase
import com.rvladimir.ttrack.auth.domain.usecase.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the login screen.
 * Follows unidirectional data flow: UI events → [login] → [uiState].
 *
 * @property loginUseCase Validates credentials, calls the backend, and persists the returned tokens.
 * @property logoutUseCase Clears persisted session tokens.
 */
class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)

    /** Observable UI state consumed by [LoginScreen]. */
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /**
     * Triggers a login request with the provided credentials.
     * On success, tokens are persisted by [LoginUseCase] before [LoginUiState.Success] is emitted.
     *
     * @param email The user's email address.
     * @param password The user's password.
     */
    fun login(
        email: String,
        password: String,
    ) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            loginUseCase(email, password)
                .onSuccess { _uiState.value = LoginUiState.Success }
                .onFailure { error ->
                    _uiState.value =
                        LoginUiState.Error(
                            message = error.message ?: "An unexpected error occurred.",
                        )
                }
        }
    }

    /** Clears persisted tokens and resets UI state to [LoginUiState.Idle]. */
    fun logout() {
        logoutUseCase()
        _uiState.value = LoginUiState.Idle
    }

    /** Resets the state back to [LoginUiState.Idle] (e.g. after error is shown). */
    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}
