package com.example.app.update

import com.example.app.AppInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.nio.file.Path
import kotlin.coroutines.cancellation.CancellationException

sealed interface UpdateState {
    data object Idle : UpdateState
    data object Checking : UpdateState
    data object UpToDate : UpdateState
    data class Available(val update: Update) : UpdateState
    data class Downloading(val update: Update, val read: Long, val total: Long) : UpdateState
    data class Ready(val update: Update, val file: Path) : UpdateState
    data class Failed(val message: String) : UpdateState
}

class UpdateManager(
    private val updater: GitHubUpdater,
    private val scope: CoroutineScope,
) {
    private val mutableState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val state: StateFlow<UpdateState> = mutableState.asStateFlow()
    private var job: Job? = null

    fun check() {
        job?.cancel()
        mutableState.value = UpdateState.Checking
        job = scope.launch {
            mutableState.value = when (val result = updater.check()) {
                UpdateCheck.UpToDate -> UpdateState.UpToDate
                is UpdateCheck.Available -> UpdateState.Available(result.update)
                is UpdateCheck.Failed -> UpdateState.Failed(result.error.message ?: result.error::class.simpleName.orEmpty())
            }
        }
    }

    fun download(update: Update) {
        job?.cancel()
        job = scope.launch {
            try {
                updater.download(update).collect { event ->
                    mutableState.value = when (event) {
                        is DownloadEvent.Progress -> UpdateState.Downloading(update, event.read, event.total)
                        is DownloadEvent.Finished -> UpdateState.Ready(update, event.file)
                    }
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                mutableState.value = UpdateState.Failed(error.message ?: error::class.simpleName.orEmpty())
            }
        }
    }

    fun install(ready: UpdateState.Ready, onLaunched: () -> Unit) {
        try {
            Installer.launch(ready.file)
            onLaunched()
        } catch (error: Exception) {
            mutableState.value = UpdateState.Failed(error.message ?: error::class.simpleName.orEmpty())
        }
    }

    fun openReleasePage(update: Update? = null) {
        Installer.openInBrowser(update?.release?.url ?: updater.releasesPage)
    }

    companion object {
        fun create(scope: CoroutineScope): UpdateManager = UpdateManager(
            GitHubUpdater(AppInfo.githubOwner, AppInfo.githubRepo, AppInfo.version),
            scope,
        )
    }
}
