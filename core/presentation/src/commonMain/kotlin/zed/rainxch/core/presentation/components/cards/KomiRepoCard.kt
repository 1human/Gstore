package zed.rainxch.core.presentation.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import zed.rainxch.core.domain.model.repository.DiscoveryPlatform
import zed.rainxch.core.presentation.components.GitHubStoreImage
import zed.rainxch.core.presentation.components.chips.KomiChip
import zed.rainxch.core.presentation.components.chips.KomiChipKind
import zed.rainxch.core.presentation.components.surfaces.KomiSurface
import zed.rainxch.core.presentation.components.surfaces.KomiSurfaceElevation
import zed.rainxch.core.presentation.components.text.KomiText
import zed.rainxch.core.presentation.components.text.KomiTextRole
import zed.rainxch.core.presentation.locals.LocalPersonality
import zed.rainxch.core.presentation.personality.ClassicPersonality
import zed.rainxch.core.presentation.personality.classicPersonality
import zed.rainxch.core.presentation.personality.model.PersonalityColors
import zed.rainxch.core.presentation.personality.utils.PersonalityPreview
import zed.rainxch.core.presentation.spacing.Spacing
import zed.rainxch.core.presentation.utils.formatReleasedAgo
import zed.rainxch.core.presentation.utils.toIcon
import zed.rainxch.core.presentation.utils.toLabel
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun KomiRepoCard(
    name: String,
    owner: String,
    language: String,
    description: String,
    platforms: ImmutableList<DiscoveryPlatform>,
    stars: Int,
    downloads: Int,
    releasedAgoDays: Int,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
    onLongPress: (() -> Unit)? = null,
    monogram: String? = null,
    imageUrl: String? = null,
    feed: KomiRepoCardFeed = KomiRepoCardFeed.Plain,
    rank: Int = 1,
    dailyStars: Int = 0,
    compact: Boolean = false,
    releasedAt: String? = null,
) {
    val colors = LocalPersonality.current.colors
    val gap = if (compact) 9.dp else 11.dp

    KomiSurface(
        modifier = modifier.fillMaxWidth(),
        elevation = KomiSurfaceElevation.Card,
        onClick = onOpen,
        onLongClick = onLongPress,
        contentPadding = PaddingValues(if (compact) 13.dp else 15.dp),
    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                CardIconTile(
                    monogram = monogram ?: name.take(2),
                    imageUrl = imageUrl,
                    size = if (compact) 40.dp else 60.dp,
                    colors = colors,
                )
                Column(modifier = Modifier.weight(1f)) {
                    KomiText(
                        text = name,
                        role = KomiTextRole.CardTitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Row(
                        modifier = Modifier.padding(top = 5.dp),
                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        KomiText(
                            text = "@$owner",
                            role = KomiTextRole.Caption,
                            color = colors.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false),
                        )
                        if (!compact) {
                            Diamond(color = colors.onSurfaceVariant)
                            KomiText(
                                text = language,
                                role = KomiTextRole.Caption,
                                color = colors.onSurfaceVariant,
                                maxLines = 1,
                            )
                        }
                    }
                }
                CardBadge(
                    feed = feed,
                    rank = rank,
                    dailyStars = dailyStars,
                    releasedAgoDays = releasedAgoDays,
                    colors = colors,
                )
            }

            KomiText(
                text = description,
                role = KomiTextRole.Body,
                color = colors.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = gap),
            )

            if (compact && platforms.isNotEmpty()) {
                Row(
                    modifier = Modifier.padding(top = gap),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    platforms.forEach { platform ->
                        platform.toIcon()?.let { icon ->
                            Icon(
                                imageVector = icon,
                                contentDescription = platform.toLabel(),
                                modifier = Modifier.size(16.dp),
                                tint = colors.onSurfaceVariant,
                            )
                        }
                    }
                }
            } else if (platforms.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.padding(top = gap),
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalArrangement = Arrangement.spacedBy(7.dp),
                ) {
                    platforms.forEach { platform ->
                        KomiChip(
                            label = platform.toLabel(),
                            kind = KomiChipKind.Info,
                            leadingContent = {
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    platform.toIcon()?.let { icon ->
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = colors.onSurface,
                                        )
                                    }
                                }
                            },
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(top = if (compact) 9.dp else 12.dp),
                thickness = 2.dp,
                color = colors.divider,
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = if (compact) 9.dp else 11.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 13.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false),
                ) {
                    Stat(
                        icon = Icons.Filled.Star,
                        value = fmtCompact(stars),
                        colors = colors,
                        hero = true,
                    )
                    Stat(
                        icon = Icons.Outlined.Download,
                        value = fmtCompact(downloads),
                        colors = colors,
                    )
                    if (!compact) {
                        formatReleasedAgo(releasedAt)?.let { ago ->
                            Stat(
                                icon = Icons.Outlined.Schedule,
                                value = ago,
                                colors = colors,
                            )
                        }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!compact) {
                        KomiText(
                            text = "Read",
                            role = KomiTextRole.LabelSmall,
                            color = colors.onSurface,
                            modifier = Modifier.padding(end = 2.dp),
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = colors.onSurface,
                    )
                }
            }
        }
    }
}

@Composable
private fun CardIconTile(
    monogram: String,
    imageUrl: String?,
    size: Dp,
    colors: PersonalityColors,
) {
    val hasImage = !imageUrl.isNullOrBlank()
    when (LocalPersonality.current) {
        is ClassicPersonality -> {
            Box(
                modifier =
                    Modifier
                        .size(size)
                        .clip(RoundedCornerShape(14.dp))
                        .background(color = colors.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                if (hasImage) {
                    GitHubStoreImage(
                        imageModel = { imageUrl },
                        modifier = Modifier.matchParentSize(),
                    )
                } else {
                    MonogramText(monogram = monogram, color = colors.onSurface, fontSize = (size.value * 0.42f).sp)
                }
            }
        }
    }
}

@Composable
private fun CardBadge(
    feed: KomiRepoCardFeed,
    rank: Int,
    dailyStars: Int,
    releasedAgoDays: Int,
    colors: PersonalityColors,
) {
    if (feed == KomiRepoCardFeed.Plain) return
    when (LocalPersonality.current) {
        is ClassicPersonality -> {
            ClassicBadge(
                feed = feed,
                rank = rank,
                dailyStars = dailyStars,
                releasedAgoDays = releasedAgoDays,
                colors = colors,
            )
        }
    }
}

@Composable
private fun ClassicBadge(
    feed: KomiRepoCardFeed,
    rank: Int,
    dailyStars: Int,
    releasedAgoDays: Int,
    colors: PersonalityColors,
) {
    val text =
        when (feed) {
            KomiRepoCardFeed.Popular -> "No. $rank"
            KomiRepoCardFeed.Trending -> "+${fmtCompact(dailyStars)}"
            KomiRepoCardFeed.Release -> "New"
            KomiRepoCardFeed.Plain -> ""
        }
    val show =
        (feed != KomiRepoCardFeed.Release || releasedAgoDays <= 7) &&
            (feed != KomiRepoCardFeed.Trending || dailyStars > 0)
    if (text.isNotEmpty() && show) {
        Box(
            modifier =
                Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(color = colors.primaryContainer)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
            KomiText(text = text, role = KomiTextRole.LabelSmall, color = colors.onPrimaryContainer)
        }
    }
}

@Composable
private fun Stat(
    icon: ImageVector,
    value: String,
    colors: PersonalityColors,
    hero: Boolean = false,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(15.dp),
            tint = if (hero) colors.primary else colors.onSurface,
        )
        KomiText(text = value, role = KomiTextRole.Caption, color = colors.onSurfaceVariant, maxLines = 1)
    }
}

// The monogram stands in for an app icon, so it scales with its tile rather than following the type scale.
@Composable
private fun MonogramText(
    monogram: String,
    color: Color,
    fontSize: TextUnit,
) {
    Text(
        text = monogram.uppercase(),
        color = color,
        maxLines = 1,
        style =
            LocalPersonality.current.type.display
                .copy(fontSize = fontSize, lineHeight = fontSize),
    )
}

@Composable
private fun Diamond(color: Color) {
    Box(modifier = Modifier.size(4.dp).rotate(45f).background(color))
}

private fun fmtCompact(n: Int): String =
    when {
        n >= 1_000_000 -> shorten(n / 1_000_000.0, n >= 10_000_000) + "M"
        n >= 1_000 -> shorten(n / 1_000.0, n >= 10_000) + "k"
        else -> n.toString()
    }

private fun shorten(
    value: Double,
    zeroDecimals: Boolean,
): String {
    if (zeroDecimals) return value.roundToInt().toString()
    val rounded = (value * 10).roundToInt() / 10.0
    return if (rounded == rounded.toLong().toDouble()) {
        rounded
            .toLong()
            .toString()
    } else {
        rounded.toString()
    }
}

@Composable
private fun PreviewCards() {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xl)) {
        KomiRepoCard(
            name = "immich",
            owner = "immich-app",
            language = "TypeScript",
            description = "High-performance self-hosted photo and video management. Back up and browse your library from any device.",
            platforms = persistentListOf(DiscoveryPlatform.Android),
            stars = 102900,
            downloads = 4946278,
            releasedAgoDays = 4,
            feed = KomiRepoCardFeed.Popular,
            rank = 1,
            dailyStars = 1820,
            onOpen = {},
        )
        KomiRepoCard(
            name = "localsend",
            owner = "localsend",
            language = "Dart",
            description = "Open-source AirDrop alternative — share files to nearby devices over your local network.",
            platforms =
                persistentListOf(
                    DiscoveryPlatform.Android,
                    DiscoveryPlatform.Windows,
                    DiscoveryPlatform.Macos,
                    DiscoveryPlatform.Linux,
                ),
            stars = 62100,
            downloads = 8330000,
            releasedAgoDays = 5,
            feed = KomiRepoCardFeed.Trending,
            dailyStars = 980,
            onOpen = {},
        )
        KomiRepoCard(
            name = "jellyfin",
            owner = "jellyfin",
            language = "C#",
            description = "The free software media system that puts you in control of streaming your media.",
            platforms = persistentListOf(DiscoveryPlatform.Android, DiscoveryPlatform.Linux),
            stars = 38400,
            downloads = 12040000,
            releasedAgoDays = 3,
            feed = KomiRepoCardFeed.Release,
            onOpen = {},
        )
    }
}

@Preview
@Composable
private fun KomiRepoCardClassicPreview() {
    PersonalityPreview(classicPersonality()) { PreviewCards() }
}
