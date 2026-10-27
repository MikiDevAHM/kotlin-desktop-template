package com.example.app.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.app.AppInfo
import com.example.app.generated.resources.about_page
import com.example.app.generated.resources.version_label
import com.example.app.ui.theme.AppTheme
import com.example.app.ui.types.res
import com.example.app.ui.types.str

@Composable
fun AboutPage() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.medium),
    ) {
        BasicText(
            res(str.about_page),
            style = AppTheme.typography.title,
        )

        BasicText(
            res(
                str.version_label,
                AppInfo.version.toString(),
            ),
            style = AppTheme.typography.body,
        )
    }
}
