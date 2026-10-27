package com.example.app.update

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest
import java.time.Duration
import kotlin.coroutines.cancellation.CancellationException

private val log = KotlinLogging.logger {}

sealed interface UpdateCheck {
    data object UpToDate : UpdateCheck
    data class Available(val update: Update) : UpdateCheck
    data class Failed(val error: Throwable) : UpdateCheck
}

sealed interface DownloadEvent {
    data class Progress(val read: Long, val total: Long) : DownloadEvent
    data class Finished(val file: Path) : DownloadEvent
}

class GitHubUpdater(
    private val owner: String,
    private val repo: String,
    private val current: Version,
    private val includePrereleases: Boolean = false,
    private val os: OsFamily = OsFamily.current,
    private val arch: Arch = Arch.current,
    private val client: HttpClient = HttpClient.newBuilder()
        .followRedirects(HttpClient.Redirect.NORMAL)
        .connectTimeout(Duration.ofSeconds(10))
        .build(),
) {
    val releasesPage: String get() = "https://github.com/$owner/$repo/releases"

    suspend fun check(): UpdateCheck = withContext(Dispatchers.IO) {
        if (owner.isBlank() || repo.isBlank()) return@withContext UpdateCheck.UpToDate
        try {
            val release = fetchRelease() ?: return@withContext UpdateCheck.UpToDate
            val version = Version.parseOrNull(release.tag) ?: return@withContext UpdateCheck.UpToDate
            if (version > current) {
                log.info { "Update available: $current -> $version" }
                UpdateCheck.Available(Update(release, version, AssetPicker.pick(release.assets, os, arch)))
            } else {
                UpdateCheck.UpToDate
            }
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            log.warn(error) { "Update check failed" }
            UpdateCheck.Failed(error)
        }
    }

    fun download(update: Update): Flow<DownloadEvent> = flow {
        val asset = requireNotNull(update.asset) { "No installer for $os in release ${update.release.tag}" }
        val target = downloadDirectory().resolve(asset.name)
        val digest = MessageDigest.getInstance("SHA-256")
        val response = client.send(request(asset.downloadUrl, "application/octet-stream"), HttpResponse.BodyHandlers.ofInputStream())
        check(response.statusCode() == 200) { "Download failed with HTTP ${response.statusCode()}" }
        val total = asset.size.takeIf { it > 0 } ?: response.headers().firstValueAsLong("Content-Length").orElse(-1)

        var read = 0L
        var lastEmitted = 0L
        response.body().use { input ->
            Files.newOutputStream(target).use { output ->
                val buffer = ByteArray(64 * 1024)
                while (true) {
                    currentCoroutineContext().ensureActive()
                    val count = input.read(buffer)
                    if (count < 0) break
                    output.write(buffer, 0, count)
                    digest.update(buffer, 0, count)
                    read += count
                    if (read - lastEmitted >= 256 * 1024) {
                        lastEmitted = read
                        emit(DownloadEvent.Progress(read, total))
                    }
                }
            }
        }

        val expected = asset.digest?.removePrefix("sha256:")
        if (expected != null && !expected.equals(digest.digest().toHexString(), ignoreCase = true)) {
            Files.deleteIfExists(target)
            error("Checksum mismatch for ${asset.name}")
        }
        emit(DownloadEvent.Progress(read, total))
        emit(DownloadEvent.Finished(target))
    }.flowOn(Dispatchers.IO)

    private fun fetchRelease(): GitHubRelease? {
        val url = if (includePrereleases) {
            "$API/repos/$owner/$repo/releases?per_page=10"
        } else {
            "$API/repos/$owner/$repo/releases/latest"
        }
        val response = client.send(request(url, "application/vnd.github+json"), HttpResponse.BodyHandlers.ofString())
        return when (val status = response.statusCode()) {
            200 -> if (includePrereleases) {
                json.decodeFromString<List<GitHubRelease>>(response.body()).firstOrNull { !it.draft }
            } else {
                json.decodeFromString<GitHubRelease>(response.body())
            }

            404 -> null
            403, 429 -> error("GitHub API rate limit reached, try again later")
            else -> error("GitHub API returned HTTP $status")
        }
    }

    private fun request(url: String, accept: String): HttpRequest = HttpRequest.newBuilder(URI.create(url))
        .header("Accept", accept)
        .header("X-GitHub-Api-Version", "2022-11-28")
        .header("User-Agent", "$repo-updater")
        .timeout(Duration.ofMinutes(10))
        .GET()
        .build()

    private fun downloadDirectory(): Path =
        Path.of(System.getProperty("java.io.tmpdir"), "$repo-update").also { Files.createDirectories(it) }

    private companion object {
        const val API = "https://api.github.com"
        val json = Json { ignoreUnknownKeys = true }
    }
}
