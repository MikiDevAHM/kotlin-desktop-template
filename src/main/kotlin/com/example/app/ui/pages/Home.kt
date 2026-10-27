package com.example.app.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.app.generated.resources.welcome
import com.example.app.generated.resources.welcome_subtitle
import com.example.app.ui.theme.AppTheme
import com.example.app.ui.types.res
import com.example.app.ui.types.str

@Composable
fun HomePage() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        BasicText(
            res(str.welcome),
            style = AppTheme.typography.title,
        )

        BasicText(
            res(str.welcome_subtitle),
            style = AppTheme.typography.body,
        )
    }
}


