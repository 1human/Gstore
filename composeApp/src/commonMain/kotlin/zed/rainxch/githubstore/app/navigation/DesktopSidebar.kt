package zed.rainxch.githubstore.app.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import zed.rainxch.core.presentation.components.badge.KomiBadge
import zed.rainxch.core.presentation.components.icon.KomiIcon
import zed.rainxch.core.presentation.components.text.KomiText
import zed.rainxch.core.presentation.components.text.KomiTextRole
import zed.rainxch.core.presentation.locals.LocalPersonality
import zed.rainxch.githubstore.core.presentation.res.Res
import zed.rainxch.githubstore.core.presentation.res.bottom_nav_apps_title
import zed.rainxch.githubstore.core.presentation.res.favourites
import zed.rainxch.githubstore.core.presentation.res.recently_viewed
import zed.rainxch.githubstore.core.presentation.res.sidebar_browse
import zed.rainxch.githubstore.core.presentation.res.tweaks_title

// Material's collapsed wide rail, wide enough for a label under each icon.
private val ClassicRailWidth = 96.dp

@Composable
fun DesktopSidebar(
    currentScreen: GithubStoreGraph?,
    onNavigate: (GithubStoreGraph) -> Unit,
    rail: Boolean,
    unreadAnnouncementsCount: Int,
    modifier: Modifier = Modifier,
) {
    val colors = LocalPersonality.current.colors
    val browseItems =
        BottomNavigationUtils.items().filterNot { it.screen == GithubStoreGraph.AppsScreen }

    // The panel is a card on the page rather than the window's own edge: every corner takes the
    // surface radius and a hairline border, the same treatment a profile card gets.
    val shape = RoundedCornerShape(LocalPersonality.current.shape.corner)

    Column(
        modifier =
            modifier
                .width(if (rail) ClassicRailWidth else 216.dp)
                .fillMaxHeight()
                .clip(shape)
                .background(colors.background)
                .border(LocalPersonality.current.shape.borderPanel, colors.outlineVariant, shape)
                .verticalScroll(rememberScrollState())
                .padding(
                    top = if (rail) 12.dp else 16.dp,
                    bottom = 16.dp,
                    start = if (rail) 8.dp else 12.dp,
                    end = if (rail) 8.dp else 12.dp,
                ),
        verticalArrangement = Arrangement.spacedBy(if (rail) 6.dp else 4.dp),
    ) {
        SidebarSideHeading(
            stringResource(Res.string.sidebar_browse),
            rail,
        )
        browseItems.forEach { item ->
            val selected = item.screen::class == currentScreen?.let { it::class }
            val badge =
                when (item.screen) {
                    GithubStoreGraph.ProfileGraph.ProfileScreen -> unreadAnnouncementsCount
                    else -> 0
                }
            SidebarNavRow(
                label = stringResource(item.titleRes),
                icon = if (selected) item.iconFilled else item.iconOutlined,
                selected = selected,
                rail = rail,
                badgeCount = badge,
                onClick = { onNavigate(item.screen) },
            )
        }

        // Library/settings/help used to live in the window menu bar, which desktop no
        // longer shows; they belong next to the other destinations.
        Spacer(Modifier.height(if (rail) 4.dp else 10.dp))
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(colors.outlineVariant),
        )
        Spacer(Modifier.height(if (rail) 2.dp else 6.dp))
        sidebarDestinations.forEach { item ->
            SidebarNavRow(
                label = stringResource(item.titleRes),
                icon = item.icon,
                selected = currentScreen != null && currentScreen::class == item.screen::class,
                rail = rail,
                badgeCount = 0,
                onClick = { onNavigate(item.screen) },
            )
        }
    }
}

private class SidebarDestination(
    val titleRes: StringResource,
    val icon: ImageVector,
    val screen: GithubStoreGraph,
)

private val sidebarDestinations: List<SidebarDestination> =
    listOf(
        SidebarDestination(Res.string.bottom_nav_apps_title, Icons.Outlined.Apps, GithubStoreGraph.AppsScreen),
        SidebarDestination(Res.string.favourites, Icons.Outlined.FavoriteBorder, GithubStoreGraph.FavouritesScreen),
        SidebarDestination(Res.string.recently_viewed, Icons.Outlined.History, GithubStoreGraph.RecentlyViewedScreen),
        SidebarDestination(Res.string.tweaks_title, Icons.Outlined.Settings, GithubStoreGraph.TweaksScreen),
    )

@Composable
private fun SidebarSideHeading(
    title: String,
    rail: Boolean,
) {
    val colors = LocalPersonality.current.colors
    if (rail) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier =
                    Modifier
                        .width(30.dp)
                        .height(2.5.dp)
                        .background(colors.outline),
            )
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 4.dp, top = 4.dp, bottom = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            KomiText(
                text = title,
                role = KomiTextRole.Overline,
                color = colors.onSurfaceVariant,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun SidebarNavRow(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    rail: Boolean,
    badgeCount: Int,
    onClick: () -> Unit,
) {
    ClassicNavRow(label = label, icon = icon, selected = selected, rail = rail, badgeCount = badgeCount, onClick = onClick)
}

// Material 3 Expressive's wide rail item: a pill behind the selected icon, the label under the
// icon when the rail is collapsed and beside it when expanded.
@Composable
private fun ClassicNavRow(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    rail: Boolean,
    badgeCount: Int,
    onClick: () -> Unit,
) {
    val colors = LocalPersonality.current.colors
    val shape = RoundedCornerShape(LocalPersonality.current.shape.cornerSmall)
    val content = if (selected) colors.onPrimaryContainer else colors.onSurface

    // Laid out by hand: the rail's own item centres the icon and the label, and a sidebar reads
    // better with both pinned to the leading edge.
    if (rail) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .then(if (selected) Modifier.background(colors.primaryContainer, shape) else Modifier)
                    .clickable(onClick = onClick)
                    .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            NavIcon(icon, content, badgeCount)
            KomiText(text = label, role = KomiTextRole.LabelSmall, color = content, maxLines = 1)
        }
    } else {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(shape)
                    .then(if (selected) Modifier.background(colors.primaryContainer, shape) else Modifier)
                    .clickable(onClick = onClick)
                    .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            NavIcon(icon, content, badgeCount)
            KomiText(
                text = label,
                role = KomiTextRole.Label,
                color = content,
                maxLines = 1,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun NavIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    badgeCount: Int,
) {
    if (badgeCount > 0) {
        Box(contentAlignment = Alignment.TopEnd) {
            KomiIcon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(17.dp),
                tint = tint,
            )
            KomiBadge(count = badgeCount, dot = true)
        }
    } else {
        KomiIcon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(17.dp),
            tint = tint,
        )
    }
}
