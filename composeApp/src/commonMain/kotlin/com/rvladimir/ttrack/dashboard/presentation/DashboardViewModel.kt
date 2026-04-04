package com.rvladimir.ttrack.dashboard.presentation

import androidx.lifecycle.ViewModel
import com.rvladimir.ttrack.auth.domain.usecase.GetUserProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

/**
 * ViewModel for the Dashboard screen.
 *
 * Reads the persisted user session and the device clock once on creation to
 * produce the initial [DashboardUiState]. No network calls are made — all
 * data is derived locally.
 *
 * @property getUserProfileUseCase Use case that reconstructs the stored session.
 * @property clock Source of the current instant — injected for testability.
 * @property timeZone Timezone used to convert the instant to a local date/time — injected for testability.
 */
class DashboardViewModel(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val clock: Clock = Clock.System,
    private val timeZone: TimeZone = TimeZone.currentSystemDefault(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(buildUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private fun buildUiState(): DashboardUiState {
        val session = getUserProfileUseCase()
        val now = clock.now().toLocalDateTime(timeZone)

        val greeting =
            when (now.hour) {
                in 5..11 -> "Good morning"
                in 12..17 -> "Good afternoon"
                else -> "Good evening"
            }

        val dayOfWeek =
            now.dayOfWeek.name
                .lowercase()
                .replaceFirstChar { it.uppercase() }
        val month =
            now.month.name
                .lowercase()
                .take(3)
                .replaceFirstChar { it.uppercase() }
        val formattedDate = "$dayOfWeek, ${now.day} $month"

        return DashboardUiState(
            userName = session?.name ?: "",
            greeting = greeting,
            formattedDate = formattedDate,
        )
    }
}
