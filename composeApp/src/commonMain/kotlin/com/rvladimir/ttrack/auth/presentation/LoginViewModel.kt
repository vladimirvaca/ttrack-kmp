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
                    repository.saveToken(result.accessToken)
                    _uiState.value = LoginUiState.Success
                }.onFailure { error ->
                    _uiState.value =
                        LoginUiState.Error(
                            message = error.message ?: "An unexpected error occurred.",
                        )
                }
        }
    }

    /** Resets the state back to [LoginUiState.Idle] (e.g. after error is shown). */
    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}
