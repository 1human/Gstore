package zed.rainxch.core.presentation.components.bars

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import zed.rainxch.core.presentation.components.badge.KomiBadge
import zed.rainxch.core.presentation.components.badge.KomiBadgedBox
import zed.rainxch.core.presentation.components.text.KomiText
import zed.rainxch.core.presentation.components.text.KomiTextRole
import zed.rainxch.core.presentation.locals.LocalPersonality
import zed.rainxch.core.presentation.personality.ClassicPersonality
import zed.rainxch.core.presentation.personality.classicPersonality
import zed.rainxch.core.presentation.personality.utils.PersonalityPreview

@Composable
fun KomiBottomBar(
    items: ImmutableList<KomiNavItem>,
    selectedId: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    ClassicBottomBar(
        items = items,
        selectedId = selectedId,
        onSelect = onSelect,
        modifier = modifier,
    )
}

@Composable
private fun ClassicBottomBar(
    items: ImmutableList<KomiNavItem>,
    selectedId: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    // On the page itself, like the top bar: the selected item's indicator is enough.
    ShortNavigationBar(modifier = modifier, containerColor = LocalPersonality.current.colors.background) {
        items.forEach { item ->
            val selected = item.id == selectedId
            ShortNavigationBarItem(
                selected = selected,
                onClick = { onSelect(item.id) },
                icon = {
                    KomiBadgedBox(
                        badge = { KomiBadge(count = item.badgeCount) },
                    ) {
                        Icon(
                            imageVector = if (selected) item.selectedIcon ?: item.icon else item.icon,
                            contentDescription = null,
                        )
                    }
                },
                label = {
                    KomiText(
                        text = item.label,
                        role = KomiTextRole.LabelSmall,
                        color = LocalContentColor.current,
                        maxLines = 1,
                    )
                },
            )
        }
    }
}

private val PreviewNav =
    persistentListOf(
        KomiNavItem(id = "home", label = "Home", icon = Icons.Default.Home),
        KomiNavItem(id = "charts", label = "Top Charts", icon = Icons.Default.Leaderboard, badgeCount = 3),
        KomiNavItem(id = "search", label = "Search", icon = Icons.Default.Search),
        KomiNavItem(id = "you", label = "You", icon = Icons.Default.Person, badgeCount = 12),
    )

@Composable
private fun PreviewBottomBar() {
    KomiBottomBar(items = PreviewNav, selectedId = "home", onSelect = {})
}

@Preview
@Composable
private fun KomiBottomBarClassicPreview() {
    PersonalityPreview(classicPersonality()) { PreviewBottomBar() }
}

@Preview
@Composable
private fun KomiBottomBarClassicDarkPreview() {
    PersonalityPreview(classicPersonality(dark = true)) { PreviewBottomBar() }
}
