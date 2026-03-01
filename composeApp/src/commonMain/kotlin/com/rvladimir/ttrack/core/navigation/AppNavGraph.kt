package com.rvladimir.ttrack.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.rvladimir.ttrack.auth.presentation.LoginScreen
import com.rvladimir.ttrack.auth.presentation.LoginViewModelFactory
import com.rvladimir.ttrack.customsets.presentation.CustomSetsScreen
import com.rvladimir.ttrack.dashboard.presentation.DashboardScreen
import com.rvladimir.ttrack.registration.presentation.RegisterScreen
import com.rvladimir.ttrack.workoutprogress.presentation.WorkoutProgressScreen
import kotlinx.serialization.Serializable

/** Typed routes for the application navigation graph. */
sealed class Screen(
    val route: String,
) {
    /** Login screen — the app's start destination when no session exists. */
    data object Login : Screen("login")

    /** Dashboard screen — shown after a successful login or on relaunch with an active session. */
    data object Dashboard : Screen("dashboard")

    /** Create account screen. */
    data object CreateAccount : Screen("create_account")

    /** Configure Routine / Timer screen. */
    data object Timer : Screen("timer")
}

/**
 * Type-safe route definition for the Workout Progress screen.
 *
 * @property prepTime Preparation duration in seconds.
 * @property workTime Work interval duration in seconds.
 * @property restTime Rest interval duration in seconds.
 * @property rounds Number of work/rest cycles.
 */
@Serializable
data class WorkoutProgressRoute(
    val prepTime: Int,
    val workTime: Int,
    val restTime: Int,
    val rounds: Int,
)

/**
 * Root navigation host that wires all top-level screens together.
 *
 * Reads the persisted session token on first composition to decide the start destination:
 * - Token present  → [Screen.Dashboard] (user was already logged in)
 * - No token       → [Screen.Login]
 *
 * On successful login the user is navigated to [Screen.Dashboard] and the Login
 * back-stack entry is removed so the system back button cannot return to it.
 */
@Composable
fun AppNavGraph() {
    val getSession = remember { LoginViewModelFactory.createGetSessionUseCase() }
    val startDestination =
        remember {
            if (getSession() != null) Screen.Dashboard.route else Screen.Login.route
        }

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate(Screen.CreateAccount.route)
                },
            )
        }

        composable(Screen.CreateAccount.route) {
            RegisterScreen(
                onRegistered = { navController.popBackStack() },
                onBack = { navController.popBackStack() },
                onLoginClick = { navController.popBackStack() },
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigate = { route -> navController.navigate(route) },
            )
        }

        composable(Screen.Timer.route) {
            CustomSetsScreen(
                onBack = { navController.popBackStack() },
                onStartWorkout = { prepTime, workTime, restTime, rounds ->
                    navController.navigate(
                        WorkoutProgressRoute(
                            prepTime = prepTime,
                            workTime = workTime,
                            restTime = restTime,
                            rounds = rounds,
                        ),
                    )
                },
            )
        }

        composable<WorkoutProgressRoute> { backStackEntry ->
            val route: WorkoutProgressRoute = backStackEntry.toRoute()
            WorkoutProgressScreen(
                prepTime = route.prepTime,
                workTime = route.workTime,
                restTime = route.restTime,
                rounds = route.rounds,
                onFinish = { navController.popBackStack() },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = false }
                    }
                },
            )
        }
    }
}
