package com.example.app.ui.icons

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.singleWindowApplication
import com.example.app.ui.theme.AppTheme

@Composable
fun LucideGallery(modifier: Modifier = Modifier) {
    val colors = AppTheme.colors
    val shape = RoundedCornerShape(8.dp)
    var query by remember { mutableStateOf("") }
    val names = remember(query) {
        val needle = query.trim().lowercase()
        Lucide.names.filter { needle.isEmpty() || needle in it }.sorted()
    }

    Column(
        modifier = modifier.fillMaxSize().background(colors.background).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        BasicTextField(
            value = query,
            onValueChange = { query = it },
            singleLine = true,
            textStyle = AppTheme.typography.body,
            cursorBrush = SolidColor(colors.accent),
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.surface, shape)
                .border(1.dp, colors.border, shape)
                .padding(12.dp),
        )
        BasicText("${names.size} names, Lucide ${Lucide.VERSION}", style = AppTheme.typography.caption)
        LazyVerticalGrid(
            columns = GridCells.Adaptive(112.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(names) { name ->
                val glyph = Lucide.byName(name) ?: return@items
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.surface, shape)
                        .padding(vertical = 12.dp, horizontal = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    LucideIcon(glyph, size = 28.dp)
                    BasicText(
                        text = name,
                        style = AppTheme.typography.caption.copy(textAlign = TextAlign.Center),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

fun main() = singleWindowApplication(title = "Lucide ${Lucide.VERSION}") {
    AppTheme { LucideGallery() }
}
