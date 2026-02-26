package com.rvladimir.ttrack.registration.presentation

/**
 * Manual DI factory for registration-related objects.
 *
 * Backend wiring (use cases, repository, API service) will be added here
 * once the registration endpoint is connected.
 */
object RegisterViewModelFactory {
    /** Creates a [RegisterViewModel] with all dependencies wired. */
    fun create(): RegisterViewModel = RegisterViewModel()
}
