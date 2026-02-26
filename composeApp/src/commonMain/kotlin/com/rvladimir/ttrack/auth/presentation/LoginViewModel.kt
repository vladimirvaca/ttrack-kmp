package com.rvladimir.ttrack.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rvladimir.ttrack.auth.domain.repository.AuthRepository
import com.rvladimir.ttrack.auth.domain.usecase.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the login screen.
 * Follows unidirectional data flow: UI events → [login] → [uiState].
 *
 * @property loginUseCase The use case that performs the login operation.
 * @property repository The auth repository, used to persist tokens and clear the session.
 */
class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val repository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)

    /** Observable UI state consumed by [LoginScreen]. */
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /**
     * Triggers a login request with the provided credentials.
     * On success both the access token and the refresh token are persisted.
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
                .onSuccess { result ->
                    repository.saveTokens(
                        accessToken = result.accessToken,
                        refreshToken = result.refreshToken,
                    )
                    _uiState.value = LoginUiState.Success
                }.onFailure { error ->
                    _uiState.value =
                        LoginUiState.Error(
                            message = error.message ?: "An unexpected error occurred.",
                        )
                }
        }
    }

    /** Clears persisted tokens and resets UI state to [LoginUiState.Idle]. */
    fun logout() {
        repository.clearTokens()
        _uiState.value = LoginUiState.Idle
    }

    /** Resets the state back to [LoginUiState.Idle] (e.g. after error is shown). */
    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}
