package com.example.app.ui.icons

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.skiaCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.app.ui.theme.AppTheme
import org.jetbrains.skia.Paint

@Composable
fun LucideIcon(
    glyph: LucideGlyph,
    modifier: Modifier = Modifier,
    size: Dp = 16.dp,
    tint: Color = AppTheme.colors.onSurface,
    contentDescription: String? = null,
) {
    val semantics = if (contentDescription != null) {
        Modifier.semantics { this.contentDescription = contentDescription }
    } else {
        Modifier
    }
    Canvas(modifier.size(size).then(semantics)) {
        val width = this.size.width
        val height = this.size.height
        val side = minOf(width, height)
        val font = LucideFont.font(side)
        drawIntoCanvas { canvas ->
            Paint().use { paint ->
                paint.color = tint.toArgb()
                canvas.skiaCanvas.drawString(glyph.text, (width - side) / 2, (height - side) / 2 + side, font, paint)
            }
        }
    }
}
