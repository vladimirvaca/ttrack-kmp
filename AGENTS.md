# AGENTS.md — Ttrack KMP

Kotlin Multiplatform training-tracker app. All UI and business logic is **shared** between Android and iOS via Compose Multiplatform. Platform-specific code is limited to `expect/actual` implementations only.

---

## Repository Layout

```
composeApp/src/
  commonMain/kotlin/com/rvladimir/ttrack/
    App.kt                     # root @Composable — wraps TTrackTheme + AppNavGraph
    core/
      navigation/AppNavGraph.kt  # single NavHost, all routes wired here
      network/AppConfig.kt       # BASE_URL constant
      network/HttpClientFactory.kt  # createKtorClient() + expect createPlatformHttpClient()
      session/SessionStorage.kt  # expect class — tokens persisted per-platform
      BackHandler.kt             # expect fun — no-op on iOS
      Platform.kt                # expect val isAndroid
    ui/theme/                  # TTrackTheme, Color.kt (named brand colors)
    auth/                      # full feature — login + token refresh
    registration/              # full feature — account creation
    dashboard/                 # presentation-only (no data/domain yet)
    workoutprogress/           # domain/model + presentation (timer state machine; no factory — ViewModel takes constructor params)
    customsets/                # presentation-only; Screen.Timer route maps here
      presentation/
        components/            # DigitScrollPicker, DurationCard, DurationPickerBottomSheet, RoundsCard, RoundsPickerBottomSheet
  androidMain/  # actual implementations + MainActivity
  iosMain/      # actual implementations + MainViewController
  commonTest/   # all unit tests live here
```

---

## Feature-First Clean Architecture

Every feature with backend interaction follows this exact layer structure:

```
<feature>/
  data/
    remote/
      <Feature>ApiService.kt       # Ktor HTTP calls only
      <Feature>Endpoints.kt        # URL constants built from AppConfig.BASE_URL
      dto/<Feature>Dtos.kt         # @Serializable @SerialName DTOs
    repository/
      <Feature>RepositoryImpl.kt   # implements domain interface
  domain/
    model/                         # pure data classes (no serialization annotations)
    repository/<Feature>Repository.kt  # interface
    usecase/<Action>UseCase.kt     # one use case per file, invoke operator
  presentation/
    <Feature>Screen.kt             # stateless @Composable
    <Feature>UiState.kt            # sealed interface: Idle | Loading | Success | Error(message)
    <Feature>ViewModel.kt          # ViewModel, MutableStateFlow<UiState>, viewModelScope
    <Feature>ViewModelFactory.kt   # object, manual DI wiring, lazy vals
```

If a feature has no backend calls (e.g. `dashboard`, `customsets`), omit `data/` and `domain/` — `presentation/` only is acceptable.

---

## Key Patterns

### Navigation
- Simple routes: `sealed class Screen(val route: String)` with `data object` entries.
- Parameterised routes: `@Serializable data class <Name>Route(val param: Type)` passed to `navController.navigate(route)`.
- Start destination is resolved at first composition by calling `GetSessionUseCase` — no token means `Screen.Login`, token present means `Screen.Dashboard`.

**Current routes in `AppNavGraph.kt`:**
| Route | Destination |
|---|---|
| `Screen.Login` | `LoginScreen` |
| `Screen.Dashboard` | `DashboardScreen` |
| `Screen.CreateAccount` | `RegisterScreen` |
| `Screen.Timer` | `CustomSetsScreen` (interval trainer configurator) |
| `WorkoutProgressRoute(prepTime, workTime, restTime, rounds)` | `WorkoutProgressScreen` (typed, `@Serializable`) |

### UiState
```kotlin
sealed interface LoginUiState {
    data object Idle : LoginUiState
    data object Loading : LoginUiState
    data object Success : LoginUiState
    data class Error(val message: String) : LoginUiState
}
```
ViewModels expose `StateFlow<XxxUiState>` via `asStateFlow()`.

### Manual DI (no Koin/Hilt yet)
Each feature has an `object <Feature>ViewModelFactory` that wires dependencies with `by lazy`. The single authenticated Ktor client is `LoginViewModelFactory.authenticatedClient` — **all features requiring auth must share this instance**.

**Exception — `workoutprogress`:** `WorkoutProgressViewModel` has no factory. It takes constructor params (`prepTime`, `workTime`, `restTime`, `rounds`) and is instantiated directly in the screen via `viewModel { WorkoutProgressViewModel(...) }`. This is correct because it has no repository dependencies — the timer state machine is pure computation. The `WorkoutUiState` data class and `WorkoutPhase` enum also live in `domain/model/WorkoutState.kt` rather than in `presentation/` as there is no separate UiState file for this feature.

### Network
- Base URL: `AppConfig.BASE_URL = "http://44.199.248.244:8080"` — change here only.
- Endpoint paths: defined in `<Feature>Endpoints` objects, not inline strings.
- Unauthenticated client: `createKtorClient()` — for login/register/public endpoints.
- Authenticated client: `LoginViewModelFactory.authenticatedClient` — auto-attaches Bearer token, silently refreshes on 401.
- Always consult the OpenAPI spec before adding/modifying endpoints: `http://44.199.248.244:8080/swagger/ttrack-be-0.3.7.yml`
- **API service interfaces:** When a feature's API service needs to be faked in tests, extract an interface (e.g. `UserApiServiceInterface` in `registration/data/remote/`). The concrete class implements it; tests create anonymous object implementations. `AuthApiService` predates this pattern and has no interface.

### expect/actual Implementations
| `expect` declaration | Android actual | iOS actual |
|---|---|---|
| `SessionStorage` | `SharedPreferences` (`ttrack_session`) | `NSUserDefaults` |
| `createPlatformHttpClient()` | OkHttp engine | Darwin engine |
| `BackHandler` | system back intercept | no-op |
| `isAndroid` | `true` | `false` |

Android `SessionStorage` requires `AppContextHolder.appContext` — this is injected in `MainActivity.onCreate` before `setContent`.

### Theme & Colors
All colors are named constants in `ui/theme/Color.kt`. Never use raw hex literals in screens — always reference these constants. Wrap all screens in `TTrackTheme`.

| Constant | Usage |
|---|---|
| `BrandGreen` | Primary accent, buttons, highlights |
| `DarkBackground` | Dark surfaces, primary text on light bg |
| `TextGray` | Secondary/muted text |
| `LightGray` | Track/divider backgrounds |
| `OffWhite` | Screen/scaffold backgrounds |
| `DeepCharcoal` | Alternative dark surface |
| `PurpleIcon` / `PurpleIconBg` | Secondary action icon + its container |
| `BarChartGreen` | Chart/progress bar fill |
| `TextGreen` | Positive/date text |
| `WorkCardBg` / `RestCardBg` / `PrepCardBg` | Phase-specific card backgrounds (workoutprogress) |
| `PrepIcon` / `WorkIcon` / `RestIcon` | Phase-specific icon tints (workoutprogress) |

---

## Build & Developer Workflow

```powershell
# Android debug APK
.\gradlew.bat :composeApp:assembleDebug

# Run all unit tests
.\gradlew.bat composeApp:testDebugUnitTest

# ktlint check (required before commit)
.\gradlew.bat ktlintCheck

# ktlint auto-fix
.\gradlew.bat ktlintFormat

# Install pre-commit hook (runs ktlint + tests on every commit)
.\gradlew.bat installGitHooks
```

iOS: open `/iosApp` in Xcode and run, or use the IDE run configuration.

---

## Testing Conventions

All tests live in `commonTest`. Patterns used throughout:

- **Fake repositories** (hand-written anonymous objects / inner classes), not mocking libraries.
- **`Dispatchers.setMain(StandardTestDispatcher())`** in `@BeforeTest` + `resetMain()` in `@AfterTest`.
- **`runTest { advanceUntilIdle() }`** to drive coroutines to completion.
- **`ktor-client-mock`** for HTTP layer tests.

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    @BeforeTest fun setUp() { Dispatchers.setMain(testDispatcher) }
    @AfterTest fun tearDown() { Dispatchers.resetMain() }
    // fake AuthRepository as anonymous object
}
```

---

## Dependencies (key versions — `gradle/libs.versions.toml`)

| Library | Version |
|---|---|
| Kotlin | 2.3.0 |
| Compose Multiplatform | 1.10.0 |
| Ktor | 3.1.3 |
| Navigation Compose | 2.9.2 |
| AndroidX Lifecycle | 2.9.6 |
| kotlinx-serialization | 1.8.1 |
| kotlinx-coroutines | 1.10.2 |
| ktlint plugin | 14.0.1 |

Add new libraries to `libs.versions.toml` first, then reference via `libs.<alias>` in `build.gradle.kts`.

