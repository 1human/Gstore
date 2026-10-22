package zed.rainxch.core.presentation.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import zed.rainxch.core.presentation.components.text.KomiText
import zed.rainxch.core.presentation.components.text.KomiTextRole
import zed.rainxch.core.presentation.locals.LocalPersonality
import zed.rainxch.core.presentation.personality.ClassicPersonality
import zed.rainxch.core.presentation.personality.classicPersonality
import zed.rainxch.core.presentation.personality.utils.PersonalityPreview
import zed.rainxch.core.presentation.spacing.Spacing

private val SegmentPill = 999.dp
private val SegmentInnerCorner = 8.dp

@Composable
fun <T> KomiSegmented(
    selected: T,
    items: ImmutableList<KomiSegmentedItem<T>>,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    size: KomiIconButtonSize = KomiIconButtonSize.Md,
) {
    ClassicSegmented(
        selected = selected,
        items = items,
        onSelect = onSelect,
        modifier = modifier,
        size = size,
    )
}

@Composable
private fun <T> ClassicSegmented(
    selected: T,
    items: ImmutableList<KomiSegmentedItem<T>>,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    size: KomiIconButtonSize,
) {
    val colors = LocalPersonality.current.colors
    val metrics = size.metrics

    Row(
        modifier = modifier.height(metrics.box),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.forEachIndexed { index, item ->
            val active = selected == item.value
            ClassicSegmentCell(
                item = item,
                active = active,
                shape = segmentShape(index = index, count = items.size, active = active),
                colors = colors,
                metrics = metrics,
                onSelect = onSelect,
            )
        }
    }
}

@Composable
private fun <T> ClassicSegmentCell(
    item: KomiSegmentedItem<T>,
    active: Boolean,
    shape: Shape,
    colors: zed.rainxch.core.presentation.personality.model.PersonalityColors,
    metrics: KomiIconButtonMetrics,
    onSelect: (T) -> Unit,
) {
    val background = if (active) colors.primary else colors.surfaceVariant
    val contentColor = if (active) colors.onPrimary else colors.onSurfaceVariant
    val interaction = remember { MutableInteractionSource() }

    Box(
        modifier =
            Modifier
                .fillMaxHeight()
                .defaultMinSize(minWidth = metrics.box)
                .clip(shape)
                .background(background)
                .clickable(interactionSource = interaction, indication = ripple()) { onSelect(item.value) }
                .padding(horizontal = Spacing.md),
        contentAlignment = Alignment.Center,
    ) {
        SegmentContent(item = item, contentColor = contentColor, iconSize = metrics.icon, textRole = KomiTextRole.Label)
    }
}

@Composable
private fun SegmentContent(
    item: KomiSegmentedItem<*>,
    contentColor: Color,
    iconSize: Dp,
    textRole: KomiTextRole,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        item.icon?.let { icon ->
            Icon(
                imageVector = icon,
                contentDescription = item.title,
                modifier = Modifier.size(iconSize),
                tint = contentColor,
            )
        }

        item.title?.let { title ->
            KomiText(
                text = title,
                role = textRole,
                color = contentColor,
                maxLines = 1,
            )
        }
    }
}

private fun segmentShape(
    index: Int,
    count: Int,
    active: Boolean,
): Shape {
    if (active || count == 1) return RoundedCornerShape(SegmentPill)
    return when (index) {
        0 -> {
            RoundedCornerShape(
                topStart = SegmentPill,
                bottomStart = SegmentPill,
                topEnd = SegmentInnerCorner,
                bottomEnd = SegmentInnerCorner,
            )
        }

        count - 1 -> {
            RoundedCornerShape(
                topStart = SegmentInnerCorner,
                bottomStart = SegmentInnerCorner,
                topEnd = SegmentPill,
                bottomEnd = SegmentPill,
            )
        }

        else -> {
            RoundedCornerShape(SegmentInnerCorner)
        }
    }
}

private val previewPaperItems =
    persistentListOf(
        KomiSegmentedItem(value = "day", title = "Day"),
        KomiSegmentedItem(value = "night", title = "Night"),
        KomiSegmentedItem(value = "nord", title = "Nord"),
    )

private val previewIconItems =
    persistentListOf(
        KomiSegmentedItem(value = "star", icon = Icons.Default.Star, title = "Star"),
        KomiSegmentedItem(value = "save", icon = Icons.Default.Bookmark, title = "Save"),
        KomiSegmentedItem(value = "like", icon = Icons.Default.Favorite, title = "Like"),
    )

private val previewLabeledItems =
    persistentListOf(
        KomiSegmentedItem(value = "auto", icon = Icons.Default.Refresh, title = "Auto"),
        KomiSegmentedItem(value = "manual", icon = Icons.Default.Settings, title = "Manual"),
    )

@Composable
private fun PreviewStack() {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.lg)) {
        KomiSegmented(selected = "night", items = previewPaperItems, onSelect = {})

        KomiSegmented(selected = "save", items = previewIconItems, onSelect = {})

        KomiSegmented(selected = "auto", items = previewLabeledItems, onSelect = {})
    }
}

@Preview
@Composable
private fun KomiSegmentedClassicPreview() {
    PersonalityPreview(classicPersonality()) { PreviewStack() }
}

@Preview
@Composable
private fun KomiSegmentedClassicDarkPreview() {
    PersonalityPreview(classicPersonality(dark = true)) { PreviewStack() }
}
