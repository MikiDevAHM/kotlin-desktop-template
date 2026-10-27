package com.example.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.app.AppInfo
import com.example.app.generated.resources.Res
import com.example.app.generated.resources.check_updates
import com.example.app.generated.resources.checking
import com.example.app.generated.resources.download_update
import com.example.app.generated.resources.downloading
import com.example.app.generated.resources.install_restart
import com.example.app.generated.resources.language_label
import com.example.app.generated.resources.open_release_page
import com.example.app.generated.resources.retry
import com.example.app.generated.resources.up_to_date
import com.example.app.generated.resources.update_available
import com.example.app.generated.resources.update_failed
import com.example.app.generated.resources.updates_label
import com.example.app.generated.resources.version_label
import com.example.app.generated.resources.welcome
import com.example.app.generated.resources.welcome_subtitle
import com.example.app.i18n.Languages
import com.example.app.ui.components.AppButton
import com.example.app.ui.components.SkiaGradient
import com.example.app.ui.theme.AppTheme
import com.example.app.update.UpdateManager
import com.example.app.update.UpdateState
import java.util.Locale
import org.jetbrains.compose.resources.stringResource

@Composable
fun App(
    updates: UpdateManager,
    onLanguageChange: (Locale) -> Unit,
    onQuit: () -> Unit,
) {
    val colors = AppTheme.colors
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SkiaGradient(
            colors = listOf(colors.accent, colors.accentAlt),
            modifier = Modifier.fillMaxWidth().height(6.dp),
        )
        BasicText(stringResource(Res.string.welcome), style = AppTheme.typography.title)
        BasicText(stringResource(Res.string.welcome_subtitle), style = AppTheme.typography.body)
        BasicText(stringResource(Res.string.version_label, AppInfo.version.toString()), style = AppTheme.typography.caption)

        Section(stringResource(Res.string.language_label)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Languages.supported.forEach { locale ->
                    AppButton(
                        text = Languages.displayName(locale),
                        onClick = { onLanguageChange(locale) },
                        primary = Languages.isSelected(locale),
                    )
                }
            }
        }

        Section(stringResource(Res.string.updates_label)) {
            UpdateSection(updates, onQuit)
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    val colors = AppTheme.colors
    val shape = RoundedCornerShape(10.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surface, shape)
            .border(1.dp, colors.border, shape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        BasicText(title, style = AppTheme.typography.caption)
        content()
    }
}

@Composable
private fun UpdateSection(updates: UpdateManager, onQuit: () -> Unit) {
    val state by updates.state.collectAsState()
    val body = AppTheme.typography.body

    when (val current = state) {
        UpdateState.Idle -> AppButton(stringResource(Res.string.check_updates), updates::check)

        UpdateState.Checking -> BasicText(stringResource(Res.string.checking), style = body)

        UpdateState.UpToDate -> {
            BasicText(stringResource(Res.string.up_to_date), style = body)
            AppButton(stringResource(Res.string.check_updates), updates::check, primary = false)
        }

        is UpdateState.Available -> {
            BasicText(stringResource(Res.string.update_available, current.update.version.toString()), style = body)
            if (current.update.asset != null) {
                AppButton(stringResource(Res.string.download_update), { updates.download(current.update) })
            } else {
                AppButton(stringResource(Res.string.open_release_page), { updates.openReleasePage(current.update) })
            }
        }

        is UpdateState.Downloading -> {
            val percent = if (current.total > 0) "${current.read * 100 / current.total}%" else "${current.read / 1024} KB"
            BasicText(stringResource(Res.string.downloading, percent), style = body)
        }

        is UpdateState.Ready -> AppButton(
            stringResource(Res.string.install_restart),
            { updates.install(current, onQuit) },
        )

        is UpdateState.Failed -> {
            BasicText(stringResource(Res.string.update_failed, current.message), style = body)
            AppButton(stringResource(Res.string.retry), updates::check, primary = false)
        }
    }
}
