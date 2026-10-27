package com.example.app.ui.components.sections

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.app.generated.resources.*
import com.example.app.ui.components.AppButton
import com.example.app.ui.theme.AppTheme
import com.example.app.ui.types.res
import com.example.app.ui.types.str
import com.example.app.update.UpdateManager
import com.example.app.update.UpdateState
import org.jetbrains.compose.resources.stringResource

@Composable
fun UpdateSection(
    updates: UpdateManager,
    onQuit: () -> Unit,
) {
    val state by updates.state.collectAsState()
    val body = AppTheme.typography.body

    when (val current = state) {
        UpdateState.Idle ->
            AppButton(res(str.check_updates), updates::check)

        UpdateState.Checking ->
            BasicText(res(str.checking), style = body)

        UpdateState.UpToDate -> {
            BasicText(res(str.up_to_date), style = body)
            AppButton(
                res(str.check_updates),
                updates::check,
                primary = false,
            )
        }

        is UpdateState.Available -> {
            BasicText(
                res(
                    str.update_available,
                    current.update.version.toString(),
                ),
                style = body,
            )

            if (current.update.asset != null) {
                AppButton(
                    res(str.download_update),
                    { updates.download(current.update) },
                )
            } else {
                AppButton(
                    res(str.open_release_page),
                    { updates.openReleasePage(current.update) },
                )
            }
        }

        is UpdateState.Downloading -> {
            val percent = if (current.total > 0) {
                "${current.read * 100 / current.total}%"
            } else {
                "${current.read / 1024} KB"
            }

            BasicText(
                stringResource(Res.string.downloading, percent),
                style = body,
            )
        }

        is UpdateState.Ready -> AppButton(
            stringResource(Res.string.install_restart),
            { updates.install(current, onQuit) },
        )

        is UpdateState.Failed -> {
            BasicText(
                stringResource(Res.string.update_failed, current.message),
                style = body,
            )

            AppButton(
                stringResource(Res.string.retry),
                updates::check,
                primary = false,
            )
        }
    }
}
