package com.example.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.app.ui.theme.AppTheme

@Composable
fun AppSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    steps: Int = 0,
    enabled: Boolean = true,
) {
    val colors = AppTheme.colors

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        BasicText(
            text = value.toInt().toString(),
            style = AppTheme.typography.caption.copy(
                color = colors.onSurfaceMuted,
            ),
        )

        Slider(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            valueRange = valueRange,
            steps = steps,
            colors = SliderDefaults.colors(
                thumbColor = colors.accent,
                activeTrackColor = colors.accent,
                inactiveTrackColor = colors.border,
                disabledThumbColor = colors.onSurfaceMuted,
                disabledActiveTrackColor = colors.onSurfaceMuted,
                disabledInactiveTrackColor = colors.border,
            ),
        )
    }
}


@Composable
fun AppCheckbox(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = AppTheme.colors

    Row(
        modifier = modifier
            .alpha(if (enabled) 1f else 0.4f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = CheckboxDefaults.colors(
                checkedColor = colors.accent,
                uncheckedColor = colors.onSurfaceMuted,
                checkmarkColor = colors.onAccent,
                disabledColor = colors.onSurfaceMuted,
                disabledIndeterminateColor = colors.border,
            ),
        )

        BasicText(
            text = text,
            style = AppTheme.typography.body.copy(
                color = colors.onSurface,
            ),
            modifier = Modifier.clickable(
                enabled = enabled,
                onClick = { onCheckedChange(!checked) },
            ),
        )
    }
}


@Composable
fun AppDropdownBox(
    options: List<String>,
    selectedOptions: Set<String>,
    onSelectionChange: (Set<String>) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Select options",
    enabled: Boolean = true,
) {
    val colors = AppTheme.colors
    var expanded by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(8.dp)

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(colors.surface)
                .border(1.dp, colors.border, shape)
                .then(
                    if (enabled) {
                        Modifier.clickable { expanded = !expanded }
                    } else {
                        Modifier
                    }
                )
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            BasicText(
                text = when (selectedOptions.size) {
                    0 -> placeholder
                    1 -> selectedOptions.first()
                    else -> "${selectedOptions.size} selected"
                },
                style = AppTheme.typography.body.copy(
                    color = if (selectedOptions.isEmpty()) {
                        colors.onSurfaceMuted
                    } else {
                        colors.onSurface
                    },
                ),
                modifier = Modifier.weight(1f),
            )

            BasicText(
                text = if (expanded) "▲" else "▼",
                style = AppTheme.typography.caption.copy(
                    color = colors.onSurfaceMuted,
                ),
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(colors.surface)
                .widthIn(min = 200.dp),
        ) {
            options.forEach { option ->
                val checked = option in selectedOptions

                DropdownMenuItem(
                    onClick = {
                        onSelectionChange(
                            if (checked) {
                                selectedOptions - option
                            } else {
                                selectedOptions + option
                            }
                        )
                    },
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = null,
                            enabled = enabled,
                        )

                        BasicText(
                            text = option,
                            style = AppTheme.typography.body.copy(
                                color = colors.onSurface,
                            ),
                        )
                    }
                }
            }
        }
    }
}
