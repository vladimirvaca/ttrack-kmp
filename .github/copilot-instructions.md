# Copilot Agent Instructions for Ttrack KMP Project

## Overview
This document provides guidelines and best practices for Copilot agents working on the Ttrack KMP (Kotlin Multiplatform) project, which uses Jetpack Compose for UI and targets both Android and iOS platforms.

---

## General Guidelines
- **Do not generate Markdown files describing what the agent did.**
- Follow the existing project structure and naming conventions.
- Prioritize code clarity, maintainability, and idiomatic Kotlin/Swift/Compose code.
- Ensure all code changes are compatible with multiplatform targets (Android/iOS) unless the task is platform-specific.
- Use shared modules (`commonMain`) for business logic and platform modules (`androidMain`, `iosMain`) for platform-specific code.
- Write concise, well-documented code and add KDoc/SwiftDoc where appropriate.
- Always run and pass ktlint checks before committing code.
- Use dependency injection and modularization where possible.
- Prefer Compose best practices: stateless composables, unidirectional data flow, and separation of UI and logic.
- For UI, use Jetpack Compose idioms (e.g., `@Composable` functions, state hoisting, theming).
- For iOS, follow SwiftUI and KMP interoperability best practices.
- Write and update tests in `commonTest` for shared logic.
- Ensure all Gradle scripts and configuration changes are cross-platform compatible.
- Use existing Gradle tasks for formatting and linting (see README for commands).
- Document any new public APIs or modules in the codebase.

---

## Commit and PR Guidelines
- Reference related issues or tasks in commit messages and PRs.
- Ensure all code is reviewed and passes CI checks before merging.
- Do not commit generated files or build artifacts.

---

## Security and Quality
- Do not expose secrets or credentials in code or configuration files.
- Validate all dependencies for known vulnerabilities.
- Keep dependencies up to date and document any major upgrades.

---

## Additional Notes
- If unsure about a change, prefer to ask for clarification or create a draft PR for discussion.
- Always check for platform-specific nuances when editing shared code.
- Do not generate Markdown files describing what the agent did (repeat for emphasis).

---

> For more details, refer to the project README and official documentation for KMP and Jetpack Compose.

