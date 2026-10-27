package com.example.app

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.example.app.generated.resources.Res
import com.example.app.generated.resources.app_icon
import com.example.app.generated.resources.app_title
import com.example.app.i18n.Languages
import com.example.app.ui.App
import com.example.app.ui.components.other.TitleBar
import com.example.app.ui.theme.AppTheme
import com.example.app.update.UpdateManager
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import java.awt.Dimension
import java.util.*

fun main() {
    Languages.restore()

    application {
        val windowState = rememberWindowState(
            size = DpSize(960.dp, 640.dp),
            position = WindowPosition(Alignment.Center),
        )
        val scope = rememberCoroutineScope()
        val updates = remember { UpdateManager.create(scope) }
        var locale by remember { mutableStateOf(Locale.getDefault()) }
        val icon = painterResource(Res.drawable.app_icon)

        Window(
            onCloseRequest = ::exitApplication,
            state = windowState,
            title = AppInfo.name,
            icon = icon,
            undecorated = true,
        ) {
            LaunchedEffect(Unit) { window.minimumSize = Dimension(640, 420) }

            AppTheme {
                key(locale) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .background(AppTheme.colors.background)
                            .border(1.dp, AppTheme.colors.border),
                    ) {
                        TitleBar(
                            state = windowState,
                            title = stringResource(Res.string.app_title),
                            icon = icon,
                            onCloseRequest = ::exitApplication,
                        )
                        App(
                            updates = updates,
                            onLanguageChange = {
                                Languages.apply(it)
                                locale = it
                            },
                            onQuit = ::exitApplication,
                        )
                    }
                }
            }
        }
    }
}
