package com.example.app.ui.components.other

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.app.generated.resources.about_page
import com.example.app.generated.resources.home_page
import com.example.app.generated.resources.settings_page
import com.example.app.ui.icons.Lucide
import com.example.app.ui.icons.LucideGlyph
import com.example.app.ui.icons.LucideIcon
import com.example.app.ui.theme.AppTheme
import com.example.app.ui.types.AppPage
import com.example.app.ui.types.res
import com.example.app.ui.types.str

@Composable
fun AppSidebar(
    currentPage: AppPage,
    onNavigate: (AppPage) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    val spacing = AppTheme.spacing

    Column(
        modifier = modifier
            .width(200.dp)
            .fillMaxHeight()
            .background(colors.surface)
            .padding(spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.small),
    ) {
        BasicText(
            text = "APP",
            style = AppTheme.typography.title,
            modifier = Modifier.padding(
                start = spacing.small,
                top = spacing.small,
                bottom = spacing.large,
            ),
        )

        SidebarItem(
            icon = Lucide.Home,
            title = res(str.home_page),
            selected = currentPage == AppPage.Home,
            onClick = { onNavigate(AppPage.Home) },
        )

        Spacer(modifier = Modifier.weight(1f))

        Divider(
            color = colors.border,
            thickness = 2.dp,
        )

        SidebarItem(
            icon = Lucide.Settings,
            title = res(str.settings_page),
            selected = currentPage == AppPage.Settings,
            onClick = { onNavigate(AppPage.Settings) },
        )

        SidebarItem(
            icon = Lucide.Info,
            title = res(str.about_page),
            selected = currentPage == AppPage.About,
            onClick = { onNavigate(AppPage.About) },
        )
    }
}

@Composable
private fun SidebarItem(
    icon: LucideGlyph? = null,
    title: String,
    selected: Boolean,
    onClick: () -> Unit,

    ) {
    val colors = AppTheme.colors
    val shape = RoundedCornerShape(8.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                if (selected) colors.accent.copy(alpha = 0.16f)
                else androidx.compose.ui.graphics.Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        icon?.let {
            LucideIcon(icon)
        }

        BasicText(
            text = title,
            style = AppTheme.typography.body.copy(
                color = if (selected) colors.accent else colors.onSurface,
            ),
            modifier = if (icon != null) Modifier.padding(start = 8.dp) else Modifier

        )
    }
}
