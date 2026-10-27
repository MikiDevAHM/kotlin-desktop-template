package com.example.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.app.ui.components.other.AppSidebar
import com.example.app.ui.components.shapes.AppGradient
import com.example.app.ui.pages.AboutPage
import com.example.app.ui.pages.HomePage
import com.example.app.ui.pages.SettingsPage
import com.example.app.ui.theme.AppTheme
import com.example.app.ui.types.AppPage
import com.example.app.update.UpdateManager
import java.util.*

@Composable
fun App(
    updates: UpdateManager,
    onLanguageChange: (Locale) -> Unit,
    onQuit: () -> Unit,
) {
    val colors = AppTheme.colors
    var currentPage by remember { mutableStateOf(AppPage.Home) }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        AppSidebar(
            currentPage = currentPage,
            onNavigate = { currentPage = it },
            modifier = Modifier.fillMaxHeight(),
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AppGradient(
                colors = listOf(colors.accent, colors.accentAlt),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            )

            when (currentPage) {
                AppPage.Home -> HomePage()
                AppPage.Settings -> SettingsPage(
                    onLanguageChange = onLanguageChange,
                    updates = updates,
                    onQuit = onQuit,
                )

                AppPage.About -> AboutPage()
            }
        }
    }
}
