package com.example.app.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.app.generated.resources.language_label
import com.example.app.generated.resources.updates_label
import com.example.app.i18n.Languages
import com.example.app.ui.components.AppButton
import com.example.app.ui.components.sections.AppSection
import com.example.app.ui.components.sections.UpdateSection
import com.example.app.ui.types.res
import com.example.app.ui.types.str
import com.example.app.update.UpdateManager
import java.util.*

@Composable
fun SettingsPage(
    onLanguageChange: (Locale) -> Unit,
    updates: UpdateManager,
    onQuit: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        AppSection(res(str.language_label)) {
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
        AppSection(res(str.updates_label)) {
            UpdateSection(updates, onQuit)
        }
    }
}
