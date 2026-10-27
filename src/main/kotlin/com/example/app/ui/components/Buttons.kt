package com.example.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import com.example.app.ui.theme.AppTheme

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    primary: Boolean = true,
) {
    val colors = AppTheme.colors
    val source = remember { MutableInteractionSource() }
    val hovered by source.collectIsHoveredAsState()
    val shape = RoundedCornerShape(8.dp)
    val background = when {
        primary -> colors.accent
        hovered -> colors.hover
        else -> colors.surface
    }
    val textColor = if (primary) colors.onAccent else colors.onSurface
    BasicText(
        text = text,
        style = AppTheme.typography.body.copy(color = textColor),
        modifier = modifier
            .alpha(if (enabled) (if (hovered && primary) 0.88f else 1f) else 0.4f)
            .clip(shape)
            .background(background)
            .then(if (primary) Modifier else Modifier.border(1.dp, colors.border, shape))
            .hoverable(source)
            .pointerHoverIcon(PointerIcon.Hand)
            .clickable(interactionSource = source, indication = null, enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    )
}

@Composable
fun AppToggleButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    primary: Boolean = true,
    checked: Boolean = false,
) {
    val colors = AppTheme.colors
    val source = remember { MutableInteractionSource() }
    val hovered by source.collectIsHoveredAsState()
    val shape = RoundedCornerShape(8.dp)

    val background = when {
        checked -> colors.accent
        hovered -> colors.hover
        else -> colors.surface
    }

    val textColor = if (checked) colors.onAccent else colors.onSurface

    BasicText(
        text = text,
        style = AppTheme.typography.body.copy(color = textColor),
        modifier = modifier
            .alpha(
                if (enabled) {
                    if (hovered && checked) 0.88f else 1f
                } else {
                    0.4f
                }
            )
            .clip(shape)
            .background(background)
            .then(
                if (primary || checked) {
                    Modifier
                } else {
                    Modifier.border(1.dp, colors.border, shape)
                }
            )
            .hoverable(source)
            .pointerHoverIcon(PointerIcon.Hand)
            .clickable(
                interactionSource = source,
                indication = null,
                enabled = enabled,
                onClick = onClick,
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
    )
}
