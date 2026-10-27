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

## Lucide icons

All [Lucide](https://lucide.dev) icons as a font, drawn with Skia.

```kotlin
LucideIcon(Lucide.Settings)                                                  // 16.dp, theme text color
LucideIcon(Lucide.FolderOpen, size = 24.dp, tint = AppTheme.colors.accent, contentDescription = "Open")
LucideIcon(Lucide.byName("arrow-up-right") ?: Lucide.CircleHelp)             // by kebab-case name at runtime
BasicText(Lucide.Heart.text, style = TextStyle(fontFamily = LucideFont.family, fontSize = 18.sp))
```

How it works: `lucide.ttf` has a 1000 unit em square with the baseline at the bottom edge, and every icon sits on
Lucide's 24x24 grid inside it. `LucideIcon` loads the TTF into a Skia `Typeface` once and draws the icon's code
point with `Canvas.drawString`, placing the baseline at the bottom of the icon box. No font metrics are involved,
so the result is identical on every OS. Prefer `LucideIcon` over `Text` with the font family, which adds line
height and can shift the glyph.

Files:

```
scripts/generate-lucide.groovy                 installs the font and generates the mapping
src/main/resources/fonts/lucide.ttf            the icon font
src/main/resources/fonts/lucide-codepoints.json  name -> code point (also contains renamed/alias names)
src/main/resources/licenses/lucide.txt         ISC + MIT notices, they must ship with the font
src/main/kotlin/.../ui/icons/Lucide.kt         generated: one typed constant per icon (Lucide.ArrowDown, ...)
src/main/kotlin/.../ui/icons/Lucide*.kt        LucideGlyph, LucideFont, LucideIcon, lookup, gallery
```

- `groovy scripts/generate-lucide.groovy` downloads the newest `lucide-static` from npm (needs `npm` and `tar`),
  `--version 1.62.0` pins a release, `--lucide-dir <path>` uses an unpacked copy. Rerun it to update the icons.
  The Kotlin package defaults to `<app.group>.ui.icons`, override it with `--package`.
- Constant names are the kebab-case name in PascalCase (`folder-open` -> `FolderOpen`). Aliases of renamed icons
  are included, so old names keep working. Icons that Lucide removed from the font (brand logos such as GitHub)
  are skipped by the script.
- `LucideGallery.kt` has a `main()` with a searchable grid of every icon: run it from the IDE to browse names.
- To remove it: delete `ui/icons/`, `scripts/generate-lucide.groovy` and the three resource files above.

## Alternatives worth knowing

- [Nucleus](https://github.com/kdroidfilter/Nucleus): Gradle plugin and runtime libraries for Compose Desktop
  (packaging, an updater, decorated window helpers, native notifications). It tracks the newest Kotlin and Compose
  releases closely, so check its requirements before adopting it.
- `compose-native` (community fork): Kotlin/Native builds of Compose without a JVM for Linux and Windows. Alpha.
