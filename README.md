# Kotlin Desktop Template

A starting point for desktop apps in Kotlin with their own window frame (no OS title bar), rendered by Skia
through Skiko. No embedded browser: Compose Desktop draws everything on a Skia canvas.

- Kotlin 2.4 on JDK 21, Gradle 9.8 (Kotlin DSL) and a version catalog (`gradle/libs.versions.toml`)
- Compose Multiplatform Desktop 1.12 (Skiko is its renderer, the Skia API is also available directly, see `Gradient.kt`)
- Undecorated window with a `TitleBar` component: drag, double-click to maximize, minimize/maximize/close, edge resize
- Auto-update from GitHub Releases, written on the JDK `HttpClient` and kotlinx.serialization (no extra HTTP library)
- i18n with Compose resources (`en`, `pt`, `de` as examples) and a persisted language choice
- Native installers through jpackage (`.msi`/`.exe`, `.dmg`, `.deb`/`.rpm`) with a bundled, jlinked JRE
- GitHub Actions for build and release, shipped commented out
- Many useful libraries ready to enable in `build.gradle.kts`, each one explained

## Quick start

```bash
./gradlew run                              # start the app
./gradlew test                             # unit tests
./gradlew packageDistributionForCurrentOS  # installer for the OS you are on (build/compose/binaries/main/)
./gradlew createDistributable              # portable folder with launcher executable and bundled JRE
```

jpackage only builds installers for the OS it runs on, so use the CI matrix in `.github/workflows/release.yml`
to produce all of them.

## Creating a project from this template

1. `gradle.properties`: `app.name`, `app.group`, `app.version`, `app.vendor`, `github.owner`, `github.repo`.
2. Rename the package `com.example.app` to your `app.group` (directories under `src/main/kotlin` and `src/test/kotlin`).
   `compose.resources.packageOfResClass` and `mainClass` already follow `app.group`.
3. `build.gradle.kts`: generate a new `upgradeUuid` for the Windows MSI and keep it stable from then on.
4. Replace the icons in `packaging/` and `src/main/composeResources/drawable/app_icon.png`.
5. `settings.gradle.kts`: change `rootProject.name`.
6. Enable the workflows: remove the leading `# ` from the lines in `.github/workflows/*.yml` and `.github/dependabot.yml`.

## Layout

```
build.gradle.kts, settings.gradle.kts, gradle.properties, gradle/libs.versions.toml
packaging/                         icons used by jpackage
src/main/composeResources/         strings (values, values-pt, values-de) and drawables
src/main/kotlin/com/example/app/
  Main.kt                          application and the single example window
  AppInfo.kt                       name/version/repo read from app.properties (filled in by Gradle)
  ui/App.kt                        example screen
  ui/theme/Theme.kt                colors, typography, light/dark
  ui/components/TitleBar.kt        custom window decoration
  ui/components/Button.kt          small example component
  ui/components/SkiaGradient.kt    drawing with the Skia API directly
  update/                          GitHub Releases updater
  i18n/, settings/                 language handling and persisted preferences
```

## Auto-update

`GitHubUpdater` calls `GET /repos/{owner}/{repo}/releases/latest`, compares the tag (`v1.2.0`) with the running
version using semantic versioning, picks the installer for the current OS and architecture, downloads it with
progress and verifies the SHA-256 digest GitHub reports for the asset. `UpdateManager` exposes the state as a
`StateFlow` for the UI, and `Installer` hands the file to the OS (`msiexec`, `open`, `xdg-open`) so the user
confirms the install, then the app quits.

- The release workflow prefixes asset names with `windows-x64`, `macos-arm64`, `macos-x64`, `linux-x64`.
  The updater matches those tokens, so keep that naming if you change the workflow.
- Linux: `.deb` or `.rpm` is chosen from `/etc/os-release`. On other distributions (Arch and derivatives) no
  installer matches, and the UI offers the release page instead.
- Only public repositories work as written. Private repositories need a token, which should not be shipped in an app.
- Unsigned installers trigger SmartScreen and Gatekeeper warnings. Signing is configured in the `windows`/`macOS` blocks.

## Internationalization

Strings live in `src/main/composeResources/values*/strings.xml` and are read with
`stringResource(Res.string.key)`. To add a language, add a `values-xx/strings.xml` and list the locale in
`Languages.supported`. The chosen language is stored with `java.util.prefs`. Switching happens at runtime by
setting the default `Locale` and recomposing the window content under `key(locale)` (see `Main.kt`).

## Custom window frame: what you get and what you give up

Drawing the title bar yourself means the OS no longer provides some behaviours:

- Edge resizing and dragging are implemented by Compose for undecorated windows. Window snapping (Windows Snap
  Layouts, macOS tiling, some Linux compositors) may not work.
- A maximized undecorated window can cover the Windows taskbar. `TitleBar` sets `maximizedBounds` to the usable
  screen area on Windows to avoid that. Test it on your multi-monitor setups.
- On macOS the controls are drawn on the left as colored circles, elsewhere on the right.
- If you need the native frame behaviour back with a custom look, JetBrains Runtime exposes a custom title bar
  API that keeps native controls and snapping, at the cost of depending on JBR internals.

## Packaging notes

- `modules(...)` in `build.gradle.kts` lists the JDK modules jlink keeps. Add what your dependencies need
  (for example `java.sql`). If something works with `run` but fails in the installed app, a missing module is the usual cause.
- Installer size is mostly the bundled runtime. Expect tens of megabytes.
- `jpackage` rejects `0.x.y` versions for `.dmg`/`.pkg`, so the template starts at `1.0.0`.

## Alternatives worth knowing

- [Nucleus](https://github.com/kdroidfilter/Nucleus): Gradle plugin and runtime libraries for Compose Desktop
  (packaging, an updater, decorated window helpers, native notifications). It tracks the newest Kotlin and Compose
  releases closely, so check its requirements before adopting it.
- `compose-native` (community fork): Kotlin/Native builds of Compose without a JVM for Linux and Windows. Alpha.
