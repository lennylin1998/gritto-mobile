# Repository Guidelines

## Project Structure & Module Organization
- Shared Kotlin source lives in `composeApp/src/commonMain/kotlin`; add platform-neutral UI and domain logic here.
- Platform folders (`composeApp/src/androidMain`, `composeApp/src/iosMain`) host target-specific Kotlin plus Android resources in `res/` and iOS wrappers.
- `composeApp/src/commonMain/composeResources` stores shared assets; keep resource keys stable across platforms.
- iOS launchers and compose previews sit in `iosApp/iosApp`; open that project in Xcode for platform integration.
- Manage dependency versions via `gradle/libs.versions.toml` to keep the Gradle catalog consistent.

## Build, Test, and Development Commands
- `./gradlew :composeApp:assembleDebug` builds the Android debug APK; run after major UI changes.
- `./gradlew :composeApp:check` executes unit tests and Gradle verification tasks across targets.
- `./gradlew :composeApp:clean` clears intermediate build outputs when syncing or version bumps fail.
- For iOS, open `iosApp/iosApp.xcodeproj` in Xcode and run the `iosApp` scheme against the desired simulator.

## Coding Style & Naming Conventions
- Follow standard Kotlin style: 4-space indentation, trailing commas for multiline collections, and explicit visibility when not public.
- Name composables and view models in PascalCase (`HomeScreen`, `SessionViewModel`); use lowerCamelCase for parameters and state holders.
- Keep package structures mirrored between `commonMain` and platform folders to ease expect/actual declarations.
- Resource names stay lower_snake_case (e.g., `ic_launcher_foreground`); update previews when changing asset identifiers.

## Testing Guidelines
- Don't write or run test.
## Commit & Pull Request Guidelines
- Don't commit or push any changes.
