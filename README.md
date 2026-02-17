# Ttrack App

Ttrack app is an app for mobile devices that allows you to track your training. It allows you to create training sessions or only track your exercise with a chronometer.

---

## Project Structure

- **/composeApp/src/commonMain/kotlin**: Shared Kotlin code for all platforms (e.g., business logic, UI components).
  - Example files: `App.kt`, `Greeting.kt`, `Platform.kt`
- **/composeApp/src/androidMain/kotlin**: Android-specific code (e.g., `MainActivity.kt`, `Platform.android.kt`).
- **/composeApp/src/iosMain/kotlin**: iOS-specific code (e.g., `MainViewController.kt`, `Platform.ios.kt`).
- **/composeApp/src/commonTest/kotlin**: Shared tests for multiplatform code.
- **/iosApp/iosApp**: iOS SwiftUI entry point and resources (`ContentView.swift`, `Info.plist`, etc.).

---

## Build and Run

### Android
- Use the run configuration in your IDE (IntelliJ IDEA or Android Studio), or run from terminal:
  - On macOS/Linux:
    ```shell
    ./gradlew :composeApp:assembleDebug
    ```
  - On Windows:
    ```shell
    .\gradlew.bat :composeApp:assembleDebug
    ```

### iOS
- Use the run configuration in your IDE, or open the `/iosApp` directory in Xcode and run the project.

---

## Code Quality

This project uses [ktlint](https://pinterest.github.io/ktlint/) for Kotlin code style checking.

### Git Hooks Setup
A pre-commit hook is provided to run ktlint before each commit. To install it:
```shell
./gradlew installGitHooks
```

### Manual ktlint Commands
- **Check code style:** `./gradlew ktlintCheck`
- **Auto-fix issues:** `./gradlew ktlintFormat`

---

## Development Environment
- **Recommended IDEs:** [IntelliJ IDEA](https://www.jetbrains.com/idea/), [Android Studio](https://developer.android.com/studio), [Xcode](https://developer.apple.com/xcode/)
- **Gradle Properties:** JVM and Android build settings are managed in `gradle.properties`.
- **Multiplatform:** Shared logic goes in `commonMain`; platform-specific logic in `androidMain` or `iosMain`.

---

> **Developed with ❤️ by vladimirvaca 👽**
