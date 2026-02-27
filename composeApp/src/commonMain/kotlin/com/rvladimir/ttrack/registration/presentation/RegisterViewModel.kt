package com.rvladimir.ttrack.registration.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rvladimir.ttrack.registration.domain.usecase.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the [RegisterScreen].
 * Follows unidirectional data flow: UI events → [register] → [uiState].
 *
 * @property registerUseCase Use case that validates input and calls the backend.
 */
class RegisterViewModel(
    private val registerUseCase: RegisterUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)

    /** Observable UI state consumed by [RegisterScreen]. */
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    /**
     * Triggers a registration request with the provided details.
     *
     * @param firstName The user's first name.
     * @param lastName The user's last name.
     * @param nickname The user's display nickname.
     * @param dateBirth The user's date of birth in ISO-8601 format `"YYYY-MM-DD"`.
     * @param email The user's email address.
     * @param password The user's chosen password.
     */
    fun register(
        firstName: String,
        lastName: String,
        nickname: String,
        dateBirth: String,
        email: String,
        password: String,
    ) {
        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading
            val result =
                registerUseCase(
                    firstName = firstName,
                    lastName = lastName,
                    nickname = nickname,
                    dateBirth = dateBirth,
                    email = email,
                    password = password,
                )
            _uiState.value =
                result.fold(
                    onSuccess = { RegisterUiState.Success },
                    onFailure = { RegisterUiState.Error(it.message ?: "An unexpected error occurred.") },
                )
        }
    }

    /** Resets the state back to [RegisterUiState.Idle] (e.g. after an error is shown). */
    fun resetState() {
        _uiState.value = RegisterUiState.Idle
    }
}
