package com.rvladimir.ttrack.dashboard.presentation

import com.rvladimir.ttrack.auth.presentation.LoginViewModelFactory

/**
 * Manual DI factory for [DashboardViewModel].
 *
 * Wires [com.rvladimir.ttrack.auth.domain.usecase.GetUserProfileUseCase] via
 * [LoginViewModelFactory] so the shared repository — and therefore the shared
 * [com.rvladimir.ttrack.core.session.SessionStorage] — is reused across features.
 */
object DashboardViewModelFactory {
    fun create(): DashboardViewModel =
        DashboardViewModel(
            getUserProfileUseCase = LoginViewModelFactory.createGetUserProfileUseCase(),
        )
}
