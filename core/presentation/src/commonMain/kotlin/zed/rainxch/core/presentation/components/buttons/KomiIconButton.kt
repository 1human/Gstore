package zed.rainxch.core.presentation.components.buttons

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import zed.rainxch.core.presentation.components.bars.LocalInBar
import zed.rainxch.core.presentation.components.overlays.KomiTooltip
import zed.rainxch.core.presentation.locals.LocalPersonality
import zed.rainxch.core.presentation.personality.ClassicPersonality
import zed.rainxch.core.presentation.personality.classicPersonality
import zed.rainxch.core.presentation.personality.utils.PersonalityPreview
import zed.rainxch.core.presentation.spacing.Spacing
import kotlin.math.roundToInt

private val MinTouchTarget = 44.dp

@Composable
fun KomiIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: KomiButtonVariant = KomiButtonVariant.Tonal,
    size: KomiIconButtonSize = KomiIconButtonSize.Md,
    enabled: Boolean = true,
    containerColor: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified,
) {
    KomiTooltip(text = contentDescription, modifier = modifier) {
        when (LocalPersonality.current) {
            is ClassicPersonality -> {
                ClassicIconButton(
                    icon = icon,
                    contentDescription = contentDescription,
                    onClick = onClick,
                    variant = variant,
                    size = size,
                    enabled = enabled,
                    containerColor = containerColor,
                    contentColor = contentColor,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ClassicIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    variant: KomiButtonVariant,
    size: KomiIconButtonSize,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified,
) {
    val colors = LocalPersonality.current.colors
    val metrics = size.metrics
    val inBar = LocalInBar.current
    // In a bar, Material's own icon button: its 40dp shape in a 48dp target, with a 24dp icon.
    val sizeModifier = if (inBar) Modifier else Modifier.size(maxOf(metrics.box, MinTouchTarget))
    val content: @Composable () -> Unit = {
        if (inBar) {
            BarIcon(icon = icon, contentDescription = contentDescription)
        } else {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(metrics.icon),
            )
        }
    }
    val resolved = if (inBar && variant != KomiButtonVariant.Destructive) KomiButtonVariant.Text else variant
    // Callers can pin the fill to a neutral surface token so a control does not read heavier than
    // its neighbours; Unspecified keeps Material's own role colours.
    val buttonColors = iconButtonColorsFor(resolved, containerColor, contentColor)

    when (resolved) {
        KomiButtonVariant.Primary -> {
            FilledIconButton(
                onClick = onClick,
                shapes = IconButtonDefaults.shapes(),
                colors = buttonColors,
                modifier = modifier.then(sizeModifier),
                enabled = enabled,
                content = content,
            )
        }

        KomiButtonVariant.Tonal -> {
            FilledTonalIconButton(
                onClick = onClick,
                shapes = IconButtonDefaults.shapes(),
                colors = buttonColors,
                modifier = modifier.then(sizeModifier),
                enabled = enabled,
                content = content,
            )
        }

        KomiButtonVariant.Outline -> {
            OutlinedIconButton(
                onClick = onClick,
                shapes = IconButtonDefaults.shapes(),
                colors = buttonColors,
                modifier = modifier.then(sizeModifier),
                enabled = enabled,
                content = content,
            )
        }

        KomiButtonVariant.Text -> {
            IconButton(
                onClick = onClick,
                shapes = IconButtonDefaults.shapes(),
                colors = buttonColors,
                modifier = modifier.then(sizeModifier),
                enabled = enabled,
                content = content,
            )
        }

        KomiButtonVariant.Destructive -> {
            FilledIconButton(
                onClick = onClick,
                shapes = IconButtonDefaults.shapes(),
                modifier = modifier.then(sizeModifier),
                enabled = enabled,
                colors =
                    IconButtonDefaults.filledIconButtonColors(
                        containerColor = colors.error,
                        contentColor = colors.onError,
                    ),
                content = content,
            )
        }
    }
}

@Composable
private fun iconButtonColorsFor(
    variant: KomiButtonVariant,
    containerColor: Color,
    contentColor: Color,
): IconButtonColors {
    val overridden = containerColor != Color.Unspecified && contentColor != Color.Unspecified
    return when (variant) {
        KomiButtonVariant.Primary,
        KomiButtonVariant.Destructive,
        -> {
            if (overridden) {
                IconButtonDefaults.filledIconButtonColors(containerColor = containerColor, contentColor = contentColor)
            } else {
                IconButtonDefaults.filledIconButtonColors()
            }
        }

        KomiButtonVariant.Tonal -> {
            if (overridden) {
                IconButtonDefaults.filledTonalIconButtonColors(containerColor = containerColor, contentColor = contentColor)
            } else {
                IconButtonDefaults.filledTonalIconButtonColors()
            }
        }

        KomiButtonVariant.Outline -> {
            if (overridden) {
                IconButtonDefaults.outlinedIconButtonColors(containerColor = containerColor, contentColor = contentColor)
            } else {
                IconButtonDefaults.outlinedIconButtonColors()
            }
        }

        KomiButtonVariant.Text -> {
            if (overridden) {
                IconButtonDefaults.iconButtonColors(containerColor = containerColor, contentColor = contentColor)
            } else {
                IconButtonDefaults.iconButtonColors()
            }
        }
    }
}

@Composable
private fun PreviewIconRow() {
    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
        KomiIconButton(Icons.AutoMirrored.Filled.ArrowBack, "Back", {}, variant = KomiButtonVariant.Tonal)
        KomiIconButton(Icons.Default.Star, "Star", {}, variant = KomiButtonVariant.Primary)
        KomiIconButton(Icons.Default.Share, "Share", {}, variant = KomiButtonVariant.Outline)
        KomiIconButton(Icons.Default.Close, "Clear", {}, variant = KomiButtonVariant.Text)
        KomiIconButton(Icons.Default.Delete, "Delete", {}, variant = KomiButtonVariant.Destructive)
    }
}

@Preview
@Composable
private fun KomiIconButtonClassicPreview() {
    PersonalityPreview(classicPersonality()) { PreviewIconRow() }
}

@Preview
@Composable
private fun KomiIconButtonClassicDarkPreview() {
    PersonalityPreview(classicPersonality(dark = true)) { PreviewIconRow() }
}
