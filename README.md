# ASTRAFORGE: COSMIC MINING

Native Android incremental mining game built with Kotlin and Jetpack Compose. The project keeps its existing game model, procedural Canvas renderers, Room save data, and progression systems; the mobile work here focuses on portrait play, responsive effects, working settings, and lifecycle/performance behavior.

## Project map

- `app/src/main/java/com/example/model/` — planets, resources, equipment, drones, upgrades, research, achievements, events, and shop models.
- `app/src/main/java/com/example/viewmodel/GameViewModel.kt` — game state, automatic extraction/economy ticks, offline rewards, and save orchestration.
- `app/src/main/java/com/example/data/` — Room database and serialization repository.
- `app/src/main/java/com/example/ui/` — Compose screens/HUD, procedural planet/equipment rendering, and particle effects.
- `app/src/main/java/com/example/audio/` — synthesized sound effects, ambient music, and haptics.

## Play on Android

- Android 7.0 (API 24) or newer.
- Portrait orientation is locked for phone play. The game starts mining automatically; tapping the world is optional and triggers a short overdrive burst.
- Use the bottom tabs for equipment, planets, inventory, and empire systems. Progress is saved locally and passive production is calculated when the app is reopened.
- Settings are opened from the top-right gear. Sound and music levels, mute, haptics, reduced animation, graphics preset, screen shake, and battery saver are retained locally. Battery saver throttles visual updates to about 20 FPS; the economy continues at its normal one-second tick.

## Build and install a debug APK

This repository does not include the Gradle wrapper. The Android Studio route is recommended:

1. Install the current Android Studio and its bundled JDK.
2. Open this repository as an Android/Gradle project and allow Gradle sync to finish. Install Android SDK Platform 36 (including the required extension level) and Android Build Tools when prompted.
3. The project uses a project-local debug signing configuration. If `debug.keystore` is not already present at the repository root, create it once from a terminal in the project root:

   ```sh
   keytool -genkeypair -v -keystore debug.keystore -storepass android \
     -alias androiddebugkey -keypass android -keyalg RSA -keysize 2048 \
     -validity 10000 -dname "CN=Android Debug,O=Android,C=US"
   ```

   This is only a local debug key and is intentionally ignored by Git. Never use it to sign a Play Store release.
4. In Android Studio choose **Build → Build Bundle(s) / APK(s) → Build APK(s)**. The debug APK is written to `app/build/outputs/apk/debug/app-debug.apk`.
5. Install it with Android Studio's Run action or, with Android platform-tools installed and a device connected with USB debugging enabled:

   ```sh
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

If you have a compatible Gradle installation on `PATH`, the equivalent build command is `gradle :app:assembleDebug` from the repository root. For a Play Store release, provide a private upload keystore using `KEYSTORE_PATH`, `STORE_PASSWORD`, and `KEY_PASSWORD`; do not commit those values or the keystore.

## Test checklist

Run the existing unit and Robolectric suites from Android Studio, or with Gradle installed:

```sh
gradle :app:testDebugUnitTest
gradle :app:connectedDebugAndroidTest
```

On a phone/emulator, verify:

1. Automatic node depletion, resource drops, XP/credits, and drone yields continue without tapping.
2. Taps trigger overdrive without blocking navigation or tab controls.
3. Equipment upgrades, planet unlock/travel, crafting/inventory, drone/facility/research/shop actions, and achievements update the UI and survive force-close/relaunch.
4. Offline rewards are bounded and appear on return.
5. Audio/mute, ambient music, vibration, graphics preset, reduced animation, shake, and battery saver settings respond immediately and survive relaunch. Music pauses when the app leaves the resumed lifecycle.
6. Settings and every tab fit at narrow phone widths and short portrait heights; verify back navigation, keyboard behavior in ASTRA-AI, and the reset confirmation flow.

## Build verification status

Source-level whitespace validation (`git diff --check`) was run in the development environment. An APK, emulator session, and Gradle test run could not be produced here: this checkout environment has no Java runtime, Gradle installation/wrapper, Android SDK, or connected Android device. Use the steps above to create and verify the APK. ASTRA-AI uses an offline tactical-response fallback when no Gemini key is configured; online responses require a valid key configured through the existing secrets setup.
