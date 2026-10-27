package com.example.app.ui.components.other

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowScope
import androidx.compose.ui.window.WindowState
import com.example.app.ui.icons.Lucide
import com.example.app.ui.icons.LucideGlyph
import com.example.app.ui.icons.LucideIcon
import com.example.app.ui.theme.AppTheme
import com.example.app.update.OsFamily
import java.awt.Frame
import java.awt.Rectangle
import java.awt.Toolkit
import java.awt.Window

val TitleBarHeight = 36.dp

@Composable
fun WindowScope.TitleBar(
    state: WindowState,
    title: String,
    onCloseRequest: () -> Unit,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    resizable: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val colors = AppTheme.colors
    val macStyle = OsFamily.current == OsFamily.MacOs

    WindowDraggableArea(
        modifier = modifier
            .fillMaxWidth()
            .height(TitleBarHeight)
            .background(colors.titleBar)
            .pointerInput(resizable) {
                if (resizable) {
                    detectTapGestures(
                        onDoubleTap = { toggleMaximize(state) }
                    )
                }
            }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (macStyle) {
                WindowControls(
                    macStyle = true,
                    maximized = state.placement == WindowPlacement.Maximized,
                    resizable = resizable,
                    onMinimize = { state.isMinimized = true },
                    onToggleMaximize = { toggleMaximize(state) },
                    onClose = onCloseRequest,
                )
            }

            if (icon != null) {
                Image(
                    icon,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .size(16.dp)
                )
            }

            BasicText(
                text = title,
                style = AppTheme.typography.caption.copy(color = colors.onSurface),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 10.dp),
            )

            Spacer(Modifier.weight(1f))
            actions()

            if (!macStyle) {
                WindowControls(
                    macStyle = false,
                    maximized = state.placement == WindowPlacement.Maximized,
                    resizable = resizable,
                    onMinimize = { state.isMinimized = true },
                    onToggleMaximize = { toggleMaximize(state) },
                    onClose = onCloseRequest,
                )
            }
        }
    }
}

private fun WindowScope.toggleMaximize(state: WindowState) {
    if (state.placement == WindowPlacement.Maximized) {
        state.placement = WindowPlacement.Floating
        return
    }
    if (OsFamily.current == OsFamily.Windows) {
        (window as? Frame)?.maximizedBounds = usableScreenBounds(window)
    }
    state.placement = WindowPlacement.Maximized
}

private fun usableScreenBounds(window: Window): Rectangle {
    val configuration = window.graphicsConfiguration
    val bounds = configuration.bounds
    val insets = Toolkit.getDefaultToolkit().getScreenInsets(configuration)
    return Rectangle(
        insets.left,
        insets.top,
        bounds.width - insets.left - insets.right,
        bounds.height - insets.top - insets.bottom,
    )
}

@Composable
private fun WindowControls(
    macStyle: Boolean,
    maximized: Boolean,
    resizable: Boolean,
    onMinimize: () -> Unit,
    onToggleMaximize: () -> Unit,
    onClose: () -> Unit,
) {
    val colors = AppTheme.colors
    if (macStyle) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TrafficLight(Color(0xFFFF5F57), onClose)
            TrafficLight(Color(0xFFFEBC2E), onMinimize)
            TrafficLight(Color(0xFF28C840), onToggleMaximize, enabled = resizable)
        }
        return
    }

    Row(Modifier.fillMaxHeight()) {
        LucideControlButton(onMinimize) { color ->
            LucideIcon(Lucide.Minus, tint = color)
        }

        if (resizable) {
            LucideControlButton(onToggleMaximize) { color ->
                if (maximized) LucideIcon(Lucide.Minimize2,tint = color) else LucideIcon(Lucide.Maximize2,tint = color)
            }
        }
        LucideControlButton(onClose, hoverBackground = colors.danger, hoverGlyph = Color.White) { color ->
            LucideIcon(Lucide.X, tint = color)
        }
    }

    /** Without Lucide
    Row(Modifier.fillMaxHeight()) {
        ControlButton(onMinimize) { color ->
            val y = size.height / 2
            drawLine(color, Offset(0f, y), Offset(size.width, y), strokeWidth = 1.dp.toPx())
        }
        if (resizable) {
            ControlButton(onToggleMaximize) { color ->
                if (maximized) drawRestoreGlyph(color) else drawMaximizeGlyph(color)
            }
        }
        ControlButton(onClose, hoverBackground = colors.danger, hoverGlyph = Color.White) { color ->
            val stroke = 1.dp.toPx()
            drawLine(color, Offset(0f, 0f), Offset(size.width, size.height), strokeWidth = stroke)
            drawLine(color, Offset(size.width, 0f), Offset(0f, size.height), strokeWidth = stroke)
        }
    }
    */
}

@Composable
private fun ControlButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    hoverBackground: Color = AppTheme.colors.hover,
    hoverGlyph: Color = AppTheme.colors.onSurface,
    glyph: DrawScope.(Color) -> Unit,
) {
    val source = remember { MutableInteractionSource() }
    val hovered by source.collectIsHoveredAsState()
    val glyphColor = if (hovered) hoverGlyph else AppTheme.colors.onSurfaceMuted
    Box(
        modifier = modifier
            .width(46.dp)
            .fillMaxHeight()
            .background(if (hovered) hoverBackground else Color.Transparent)
            .hoverable(source)
            .clickable(interactionSource = source, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(10.dp)) { glyph(glyphColor) }
    }
}

@Composable
private fun LucideControlButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    hoverBackground: Color = AppTheme.colors.hover,
    hoverGlyph: Color = AppTheme.colors.onSurface,
    content: @Composable (Color) -> Unit,
) {
    val source = remember { MutableInteractionSource() }
    val hovered by source.collectIsHoveredAsState()
    val glyphColor = if (hovered) hoverGlyph else AppTheme.colors.onSurfaceMuted
    Box(
        modifier = modifier
            .width(46.dp)
            .fillMaxHeight()
            .background(if (hovered) hoverBackground else Color.Transparent)
            .hoverable(source)
            .clickable(interactionSource = source, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        content(glyphColor)
    }
}

@Composable
private fun TrafficLight(color: Color, onClick: () -> Unit, enabled: Boolean = true) {
    Box(
        Modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(if (enabled) color else AppTheme.colors.border)
            .clickable(enabled = enabled, onClick = onClick),
    )
}

private fun DrawScope.drawMaximizeGlyph(color: Color) {
    val stroke = 1.dp.toPx()
    drawRect(
        color = color,
        topLeft = Offset(stroke / 2, stroke / 2),
        size = Size(size.width - stroke, size.height - stroke),
        style = Stroke(stroke),
    )
}

private fun DrawScope.drawRestoreGlyph(color: Color) {
    val stroke = 1.dp.toPx()
    val offset = 2.dp.toPx()
    drawRect(
        color = color,
        topLeft = Offset(stroke / 2, offset + stroke / 2),
        size = Size(size.width - offset - stroke, size.height - offset - stroke),
        style = Stroke(stroke),
    )
    val back = Path().apply {
        moveTo(offset + stroke / 2, offset)
        lineTo(offset + stroke / 2, stroke / 2)
        lineTo(size.width - stroke / 2, stroke / 2)
        lineTo(size.width - stroke / 2, size.height - offset - stroke / 2)
        lineTo(size.width - offset, size.height - offset - stroke / 2)
    }
    drawPath(back, color, style = Stroke(stroke))
}
