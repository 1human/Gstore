package zed.rainxch.core.presentation.components.lists

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import zed.rainxch.core.presentation.components.icon.KomiIcon
import zed.rainxch.core.presentation.components.inputs.KomiSwitch
import zed.rainxch.core.presentation.components.text.KomiText
import zed.rainxch.core.presentation.components.text.KomiTextRole
import zed.rainxch.core.presentation.locals.LocalPersonality
import zed.rainxch.core.presentation.personality.ClassicPersonality
import zed.rainxch.core.presentation.personality.classicPersonality
import zed.rainxch.core.presentation.personality.utils.PersonalityPreview
import zed.rainxch.core.presentation.spacing.Spacing

private val ClassicDividerInset = 56.dp
private const val DISABLED_ALPHA = 0.45f

// Classic draws Material 3 Expressive's segmented list: one tonal tile per row, 2dp apart,
// with the outer corners rounded more than the inner ones.
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun KomiList(
    items: ImmutableList<KomiListItem>,
    modifier: Modifier = Modifier,
) {
    val personality = LocalPersonality.current
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
    ) {
        items.forEachIndexed { index, item ->
            ItemRow(
                item = item,
                showDivider = false,
                modifier =
                    Modifier
                        .clip(ListItemDefaults.segmentedShapes(index, items.size).shape)
                        .background(personality.colors.surfaceContainer),
            )
        }
    }
}

@Composable
private fun ItemRow(
    item: KomiListItem,
    showDivider: Boolean,
    modifier: Modifier = Modifier,
) {
    KomiListRow(
        title = item.title,
        modifier = modifier,
        subtitle = item.subtitle,
        icon = item.icon,
        onClick = rowClick(item),
        onLongClick = item.onLongClick,
        destructive = item.destructive,
        selected = item.selected,
        enabled = item.enabled,
        showDivider = showDivider,
        trailing = { RowTrailing(item.trailing) },
    )
}

@Composable
fun KomiListContainer(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val personality = LocalPersonality.current
    val colors = personality.colors
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(personality.shape.corner))
                .background(color = colors.surfaceContainer)
                .padding(6.dp),
        content = content,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun KomiListRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: ImageVector? = null,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    strong: Boolean = false,
    destructive: Boolean = false,
    selected: Boolean = false,
    enabled: Boolean = true,
    showDivider: Boolean = false,
    trailing: @Composable (() -> Unit)? = null,
) {
    val personality = LocalPersonality.current
    val colors = personality.colors
    val shape = personality.shape
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val titleColor =
        when {
            destructive -> colors.error
            selected -> colors.onPrimaryContainer
            else -> colors.onSurface
        }
    val subtitleColor =
        if (selected) colors.onPrimaryContainer else colors.onSurfaceVariant
    val clickModifier =
        if (onClick == null && onLongClick == null) {
            Modifier
        } else {
            Modifier.combinedClickable(enabled = enabled, onLongClick = onLongClick, onClick = onClick ?: {})
        }
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(shape.cornerSmall))
                .then(rowFill(selected, hovered, colors.primaryContainer))
                .then(rowDivider(showDivider, icon != null, colors.outlineVariant))
                .then(clickModifier)
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .alpha(if (enabled) 1f else DISABLED_ALPHA),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (icon != null) {
            RowLeadingIcon(
                icon = icon,
                selected = selected,
                destructive = destructive,
                colors = colors,
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            KomiText(
                text = title,
                role = if (strong) KomiTextRole.CardTitle else KomiTextRole.ItemTitle,
                color = titleColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            subtitle?.let {
                KomiText(
                    text = it,
                    role = KomiTextRole.Body,
                    color = subtitleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        trailing?.invoke()
    }
}

@Composable
private fun RowLeadingIcon(
    icon: ImageVector,
    selected: Boolean,
    destructive: Boolean,
    colors: zed.rainxch.core.presentation.personality.model.PersonalityColors,
) {
    Box(
        modifier =
            Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (selected) colors.primary else colors.surfaceContainerHigh),
        contentAlignment = Alignment.Center,
    ) {
        KomiIcon(
            imageVector = icon,
            contentDescription = null,
            tint =
                when {
                    selected -> colors.onPrimary
                    destructive -> colors.error
                    else -> colors.onSurfaceVariant
                },
            modifier = Modifier.size(22.dp),
        )
    }
}

private fun rowFill(
    selected: Boolean,
    hovered: Boolean,
    fill: androidx.compose.ui.graphics.Color,
): Modifier =
    if (selected || hovered) {
        Modifier.background(fill)
    } else {
        Modifier
    }

private fun rowDivider(
    show: Boolean,
    hasLeading: Boolean,
    color: androidx.compose.ui.graphics.Color,
): Modifier =
    if (!show) {
        Modifier
    } else {
        Modifier.drawBehind {
            val stroke = 1.dp.toPx()
            val startX = if (hasLeading) ClassicDividerInset.toPx() else 0f
            val y = size.height - stroke / 2f
            drawLine(
                color = color,
                start = Offset(startX, y),
                end = Offset(size.width, y),
                strokeWidth = stroke,
            )
        }
    }

@Composable
private fun RowTrailing(trailing: KomiListTrailing) {
    val colors = LocalPersonality.current.colors
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when (trailing) {
            KomiListTrailing.None -> {
                Unit
            }

            KomiListTrailing.Chevron -> {
                Chevron()
            }

            is KomiListTrailing.Value -> {
                KomiText(text = trailing.text, role = KomiTextRole.Mono, color = colors.onSurfaceVariant, maxLines = 1)

                Chevron()
            }

            is KomiListTrailing.Badge -> {
                Box(
                    modifier =
                        Modifier
                            .clip(RoundedCornerShape(50))
                            .background(colors.error)
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    KomiText(
                        text = trailing.count.toString(),
                        role = KomiTextRole.Mono,
                        color = colors.onError,
                        maxLines = 1,
                    )
                }

                Chevron()
            }

            KomiListTrailing.UnreadDot -> {
                Box(
                    modifier =
                        Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(colors.error),
                )

                Chevron()
            }

            is KomiListTrailing.Toggle -> {
                KomiSwitch(checked = trailing.checked, onCheckedChange = null)
            }
        }
    }
}

@Composable
private fun Chevron() {
    KomiIcon(
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
        contentDescription = null,
        tint = LocalPersonality.current.colors.onSurfaceVariant,
        modifier = Modifier.size(20.dp),
    )
}

private fun rowClick(item: KomiListItem): () -> Unit {
    val trailing = item.trailing
    return if (trailing is KomiListTrailing.Toggle) {
        { trailing.onCheckedChange(!trailing.checked) }
    } else {
        item.onClick
    }
}

@Composable
private fun PreviewLists() {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.lg)) {
        KomiList(
            items =
                persistentListOf(
                    KomiListItem(
                        title = "Stars",
                        subtitle = "Your starred repositories from GitHub",
                        icon = Icons.Outlined.Star,
                        selected = true,
                        onClick = {},
                    ),
                    KomiListItem(
                        title = "Favourites",
                        subtitle = "Saved locally",
                        icon = Icons.Outlined.Favorite,
                        trailing = KomiListTrailing.Value("24"),
                        onClick = {},
                    ),
                    KomiListItem(
                        title = "Recently Viewed",
                        subtitle = "Repositories you have visited",
                        icon = Icons.Outlined.Schedule,
                        onClick = {},
                    ),
                ),
        )

        KomiList(
            items =
                persistentListOf(
                    KomiListItem(
                        title = "What's New",
                        subtitle = "Recent releases",
                        icon = Icons.Outlined.Campaign,
                        trailing = KomiListTrailing.Badge(3),
                        onClick = {},
                    ),
                    KomiListItem(
                        title = "Announcements",
                        subtitle = "News and notices",
                        icon = Icons.Outlined.Notifications,
                        trailing = KomiListTrailing.UnreadDot,
                        onClick = {},
                    ),
                    KomiListItem(
                        title = "Push notifications",
                        subtitle = "Toggles in place",
                        icon = Icons.Outlined.Notifications,
                        trailing = KomiListTrailing.Toggle(checked = true, onCheckedChange = {}),
                        onClick = {},
                    ),
                    KomiListItem(
                        title = "Logout",
                        icon = Icons.AutoMirrored.Filled.Logout,
                        trailing = KomiListTrailing.None,
                        destructive = true,
                        onClick = {},
                    ),
                ),
        )
    }
}

@Preview
@Composable
private fun KomiListClassicPreview() {
    PersonalityPreview(classicPersonality()) { PreviewLists() }
}

@Preview
@Composable
private fun KomiListClassicDarkPreview() {
    PersonalityPreview(classicPersonality(dark = true)) { PreviewLists() }
}
