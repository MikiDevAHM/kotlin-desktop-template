import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose)

    // Compose Hot Reload: edit a composable and see it update in the running window without restarting.
    // alias(libs.plugins.compose.hot.reload)

    // Static analysis (configure rules in config/detekt.yml): ./gradlew detekt
    // alias(libs.plugins.detekt)

    // Code formatting: ./gradlew spotlessApply (then add a spotless { kotlin { ktlint() } } block)
    // alias(libs.plugins.spotless)
}

fun property(name: String): String = providers.gradleProperty(name).get()

group = property("app.group")
version = property("app.version")

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.ui)
    implementation(compose.components.resources)

    implementation(libs.bundles.kotlinx)
    implementation(libs.kotlin.logging)
    runtimeOnly(libs.logback.classic)

    // ---------------------------------------------------------------------------------------------
    // Optional libraries. Uncomment the line you need, the catalog entries already exist.
    // ---------------------------------------------------------------------------------------------

    // OkHttp: the de-facto HTTP client for the JVM. HTTP/2, connection pooling, interceptors, WebSockets,
    // caching and sensible timeouts. Use it directly for simple REST calls and downloads.
    // implementation(libs.okhttp)
    // implementation(libs.okhttp.logging)   // interceptor that logs requests/responses, great while debugging

    // Ktor client: Kotlin-first HTTP client built on coroutines with plugins (JSON content negotiation,
    // logging, retries, auth). Runs on top of the OkHttp engine here. Pick Ktor OR plain OkHttp, not both.
    // implementation(libs.bundles.ktor.client)

    // kotlinx-datetime: multiplatform date/time API (Instant, LocalDate, TimeZone) with Kotlin-friendly types.
    // implementation(libs.kotlinx.datetime)

    // Koin: lightweight dependency injection with a Kotlin DSL, no code generation. koin-compose adds
    // koinInject() / KoinApplication for composables.
    // implementation(libs.bundles.koin)

    // Coil 3: async image loading for Compose (URLs, files, caching, SVG via extra modules).
    // implementation(libs.bundles.coil)

    // Okio / kotlinx-io: fast, ergonomic file and stream I/O (buffers, hashing, gzip, FileSystem abstraction).
    // implementation(libs.okio)
    // implementation(libs.kotlinx.io.core)

    // Clikt: command line argument parsing for when the app also needs to run from a terminal.
    // implementation(libs.clikt)

    // JNA: call native OS libraries (Win32, macOS frameworks, libc) from Kotlin without writing JNI.
    // Useful for DWM effects, taskbar integration, registry access and so on.
    // implementation(libs.jna)
    // implementation(libs.jna.platform)   // ready-made Win32 / macOS / X11 mappings

    // directories-jvm: correct per-OS config, cache and data folders (%APPDATA%, ~/Library, XDG).
    // implementation(libs.directories)

    // Local database: SQLite through JDBC, with Exposed as a Kotlin SQL DSL / DAO layer on top.
    // implementation(libs.sqlite.jdbc)
    // implementation(libs.bundles.exposed)

    // Arrow: functional building blocks (typed errors with Either/Raise, Option, resilience helpers).
    // implementation(libs.arrow.core)

    // Material 3 components and extended icons, if you would rather not build your own components.
    // implementation(compose.material3)
    // implementation(compose.materialIconsExtended)

    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.kotlinx.coroutines.test)
    testRuntimeOnly(platform(libs.junit.bom))
    testRuntimeOnly(libs.junit.platform.launcher)

    // MockK: mocking library designed for Kotlin (final classes, coroutines, extension functions).
    // testImplementation(libs.mockk)

    // Turbine: small testing helper for kotlinx.coroutines Flow / StateFlow emissions.
    // testImplementation(libs.turbine)

    // Compose UI tests on desktop (JUnit4 rule based): createComposeRule()
    // testImplementation(compose.desktop.uiTestJUnit4)
}

compose.resources {
    packageOfResClass = "${property("app.group")}.generated.resources"
    publicResClass = false
}

compose.desktop {
    application {
        mainClass = "${property("app.group")}.MainKt"

        jvmArgs += listOf("-Xms64m", "-Xmx512m")
        // Fallback when GPU rendering misbehaves (VMs, some Linux drivers): jvmArgs += "-Dskiko.renderApi=SOFTWARE"

        nativeDistributions {
            targetFormats(
                TargetFormat.Msi,
                TargetFormat.Exe,
                TargetFormat.Dmg,
                TargetFormat.Deb,
                TargetFormat.Rpm,
            )
            packageName = property("app.name")
            packageVersion = property("app.version")
            description = property("app.description")
            vendor = property("app.vendor")
            copyright = property("app.copyright")

            // jlink only keeps the JDK modules it detects. java.net.http is used by the updater and
            // jdk.crypto.ec is required for TLS to GitHub on JDK 21. Add more here (e.g. "java.sql").
            modules("java.net.http", "jdk.crypto.ec", "jdk.unsupported")

            windows {
                iconFile.set(project.file("packaging/icon.ico"))
                menuGroup = property("app.name")
                shortcut = true
                perUserInstall = true
                dirChooser = true
                // Keep this UUID stable for the life of your app, it lets new MSI versions upgrade old ones.
                // Generate a new one for every project you create from this template.
                upgradeUuid = "e29424c7-601e-4466-b348-67ace1b93274"
            }
            macOS {
                iconFile.set(project.file("packaging/icon.icns"))
                bundleID = property("app.group")
                dockName = property("app.name")
                // Signing / notarization: set signing { sign.set(true) } and notarization { ... } here.
            }
            linux {
                iconFile.set(project.file("packaging/icon.png"))
                shortcut = true
                menuGroup = property("app.name")
            }
        }

        // Obfuscation / shrinking of the release build with ProGuard:
        // buildTypes.release.proguard {
        //     isEnabled = true
        //     configurationFiles.from(project.file("proguard-rules.pro"))
        // }
    }
}

tasks.processResources {
    val values = mapOf(
        "name" to property("app.name"),
        "version" to property("app.version"),
        "githubOwner" to property("github.owner"),
        "githubRepo" to property("github.repo"),
    )
    inputs.properties(values)
    filesMatching("app.properties") { expand(values) }
}

tasks.test {
    useJUnitPlatform()
}
