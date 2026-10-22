package zed.rainxch.feed.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import zed.rainxch.core.domain.model.repository.FeedCategory
import zed.rainxch.core.presentation.components.text.KomiText
import zed.rainxch.core.presentation.components.text.KomiTextRole
import zed.rainxch.core.presentation.locals.LocalPersonality
import zed.rainxch.core.presentation.utils.toLabel

// Desktop has no sidebar category list, so the feed carries the categories above the grid in the
// same segmented shape the charts screen uses for its tabs. Eight labels do not fit one row at
// equal widths, so the segments keep their own width and the row scrolls.
@Composable
fun FeedCategoryTabs(
    categories: ImmutableList<FeedCategory>,
    selected: FeedCategory,
    onSelect: (FeedCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth().padding(vertical = 6.dp),
        contentPadding = PaddingValues(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(categories, key = { it.name }) { category ->
            CategorySegment(
                label = category.toLabel(),
                selected = category == selected,
                onClick = { onSelect(category) },
            )
        }
    }
}

@Composable
private fun CategorySegment(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val personality = LocalPersonality.current
    val colors = personality.colors
    val shape = RoundedCornerShape(personality.shape.cornerSmall)

    Row(
        modifier =
            Modifier
                .clip(shape)
                .background(if (selected) colors.primary else colors.surface)
                .border(BorderStroke(1.dp, colors.outline), shape)
                .clickable(onClick = onClick)
                .padding(vertical = 8.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        KomiText(
            text = label,
            role = KomiTextRole.Label,
            color = if (selected) colors.onPrimary else colors.onSurface,
            maxLines = 1,
        )
    }
}
