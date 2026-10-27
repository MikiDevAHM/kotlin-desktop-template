package com.example.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Immutable
data class AppColors(
    val background: Color,
    val surface: Color,
    val titleBar: Color,
    val onSurface: Color,
    val onSurfaceMuted: Color,
    val accent: Color,
    val accentAlt: Color,
    val onAccent: Color,
    val hover: Color,
    val danger: Color,
    val border: Color,
)

@Immutable
data class AppTypography(
    val title: TextStyle,
    val body: TextStyle,
    val caption: TextStyle,
)

@Immutable
data class AppSpacing(
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp,
)

private val DarkColors = AppColors(
    background = Color(0xFF14161A),
    surface = Color(0xFF1D2025),
    titleBar = Color(0xFF101215),
    onSurface = Color(0xFFE6E8EB),
    onSurfaceMuted = Color(0xFF9AA0A8),
    accent = Color(0xFF6C8CFF),
    accentAlt = Color(0xFFB46CFF),
    onAccent = Color(0xFF0B0D10),
    hover = Color(0x1FFFFFFF),
    danger = Color(0xFFE81123),
    border = Color(0xFF2A2E35),
)

private val LightColors = AppColors(
    background = Color(0xFFF6F7F9),
    surface = Color(0xFFFFFFFF),
    titleBar = Color(0xFFECEEF1),
    onSurface = Color(0xFF1B1E23),
    onSurfaceMuted = Color(0xFF5B616B),
    accent = Color(0xFF3D5AFE),
    accentAlt = Color(0xFF8E3DFF),
    onAccent = Color(0xFFFFFFFF),
    hover = Color(0x14000000),
    danger = Color(0xFFE81123),
    border = Color(0xFFD9DCE1),
)

private val LocalColors = staticCompositionLocalOf { DarkColors }

private val LocalTypography = staticCompositionLocalOf {
    AppTypography(
        TextStyle.Default,
        TextStyle.Default,
        TextStyle.Default,
    )
}

private val LocalSpacing = staticCompositionLocalOf {
    AppSpacing()
}

object AppTheme {
    val colors: AppColors
        @Composable @ReadOnlyComposable get() = LocalColors.current

    val typography: AppTypography
        @Composable @ReadOnlyComposable get() = LocalTypography.current

    val spacing: AppSpacing
        @Composable @ReadOnlyComposable get() = LocalSpacing.current
}

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkColors else LightColors

    val typography = remember(colors) {
        AppTypography(
            title = TextStyle(
                color = colors.onSurface,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
            ),
            body = TextStyle(
                color = colors.onSurface,
                fontSize = 14.sp,
            ),
            caption = TextStyle(
                color = colors.onSurfaceMuted,
                fontSize = 12.sp,
            ),
        )
    }

    val spacing = remember { AppSpacing() }

    CompositionLocalProvider(
        LocalColors provides colors,
        LocalTypography provides typography,
        LocalSpacing provides spacing,
        content = content,
    )
}
