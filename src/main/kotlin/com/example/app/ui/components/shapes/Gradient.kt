package com.example.app.ui.components.shapes

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.skiaCanvas
import org.jetbrains.skia.*

@Composable
fun AppGradient(colors: List<Color>, modifier: Modifier = Modifier) {
    val gradient = Gradient(
        colors = Gradient.Colors(
            colors.map { Color4f(it.red, it.green, it.blue, it.alpha) }.toTypedArray(),
            tileMode = FilterTileMode.CLAMP
        )
    )

    Canvas(modifier) {
        drawIntoCanvas { canvas ->
            Paint().use {
                it.shader = Shader.makeLinearGradient(0f, 0f, size.width, 0f, gradient)
                canvas.skiaCanvas.drawRect(Rect.makeWH(size.width, size.height), it)
            }
        }
    }
}
