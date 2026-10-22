@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import zed.rainxch.core.presentation.components.text.KomiText
import zed.rainxch.core.presentation.components.text.KomiTextRole
import zed.rainxch.core.presentation.locals.LocalPersonality
import zed.rainxch.core.presentation.personality.ClassicPersonality
import zed.rainxch.core.presentation.personality.classicPersonality
import zed.rainxch.core.presentation.personality.utils.PersonalityPreview
import zed.rainxch.core.presentation.spacing.Spacing
import kotlin.math.roundToInt

@Composable
fun KomiButton(
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    variant: KomiButtonVariant = KomiButtonVariant.Primary,
    size: KomiButtonSize = KomiButtonSize.Md,
    enabled: Boolean = true,
    loading: Boolean = false,
    fullWidth: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    containerColor: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified,
) {
    when (val personality = LocalPersonality.current) {
        is ClassicPersonality -> {
            ClassicButton(
                personality = personality,
                onClick = onClick,
                label = label,
                modifier = modifier,
                variant = variant,
                size = size,
                enabled = enabled,
                loading = loading,
                fullWidth = fullWidth,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                containerColor = containerColor,
                contentColor = contentColor,
            )
        }
    }
}

@Composable
private fun ClassicButton(
    personality: ClassicPersonality,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    variant: KomiButtonVariant,
    size: KomiButtonSize,
    enabled: Boolean,
    loading: Boolean,
    fullWidth: Boolean,
    leadingIcon: ImageVector?,
    trailingIcon: ImageVector?,
    containerColor: Color,
    contentColor: Color,
) {
    val colors = personality.colors
    val metrics = buttonMetrics(size)
    val shapes = ButtonDefaults.shapesFor(metrics.height)
    val active = enabled && !loading
    val contentPadding = PaddingValues(horizontal = metrics.hPadding)
    val buttonModifier =
        modifier
            .heightIn(min = metrics.height)
            .then(if (fullWidth) Modifier.fillMaxWidth() else Modifier)

    val overrideColors =
        if (containerColor != Color.Unspecified && contentColor != Color.Unspecified) {
            when (variant) {
                KomiButtonVariant.Primary,
                KomiButtonVariant.Destructive,
                -> {
                    ButtonDefaults.buttonColors(
                        containerColor = containerColor,
                        contentColor = contentColor,
                    )
                }

                KomiButtonVariant.Tonal -> {
                    ButtonDefaults.filledTonalButtonColors(
                        containerColor = containerColor,
                        contentColor = contentColor,
                    )
                }

                KomiButtonVariant.Outline -> {
                    ButtonDefaults.outlinedButtonColors(
                        containerColor = containerColor,
                        contentColor = contentColor,
                    )
                }

                KomiButtonVariant.Text -> {
                    ButtonDefaults.textButtonColors(
                        containerColor = containerColor,
                        contentColor = contentColor,
                    )
                }
            }
        } else {
            null
        }

    val content: @Composable RowScope.() -> Unit = {
        KomiButtonContent(
            label = label,
            contentColor = LocalContentColor.current,
            iconSize = metrics.icon,
            gap = metrics.gap,
            labelRole = metrics.labelRole,
            loading = loading,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
        )
    }

    when (variant) {
        KomiButtonVariant.Primary -> {
            Button(
                onClick = onClick,
                shapes = shapes,
                modifier = buttonModifier,
                enabled = active,
                contentPadding = contentPadding,
                colors = overrideColors ?: ButtonDefaults.buttonColors(),
                content = content,
            )
        }

        KomiButtonVariant.Tonal -> {
            FilledTonalButton(
                onClick = onClick,
                shapes = shapes,
                modifier = buttonModifier,
                enabled = active,
                contentPadding = contentPadding,
                colors = overrideColors ?: ButtonDefaults.filledTonalButtonColors(),
                content = content,
            )
        }

        KomiButtonVariant.Outline -> {
            OutlinedButton(
                onClick = onClick,
                shapes = shapes,
                modifier = buttonModifier,
                enabled = active,
                contentPadding = contentPadding,
                colors = overrideColors ?: ButtonDefaults.outlinedButtonColors(),
                content = content,
            )
        }

        KomiButtonVariant.Text -> {
            TextButton(
                onClick = onClick,
                shapes = shapes,
                modifier = buttonModifier,
                enabled = active,
                contentPadding = contentPadding,
                colors = overrideColors ?: ButtonDefaults.textButtonColors(),
                content = content,
            )
        }

        KomiButtonVariant.Destructive -> {
            Button(
                onClick = onClick,
                shapes = shapes,
                modifier = buttonModifier,
                enabled = active,
                contentPadding = contentPadding,
                colors =
                    overrideColors
                        ?: ButtonDefaults.buttonColors(
                            containerColor = colors.error,
                            contentColor = colors.onError,
                        ),
                content = content,
            )
        }
    }
}

@Composable
private fun RowScope.KomiButtonContent(
    label: String,
    contentColor: Color,
    iconSize: Dp,
    gap: Dp,
    labelRole: KomiTextRole,
    loading: Boolean,
    leadingIcon: ImageVector?,
    trailingIcon: ImageVector?,
) {
    CompositionLocalProvider(LocalContentColor provides contentColor) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(iconSize),
                strokeWidth = 2.dp,
                color = contentColor,
            )
            Spacer(modifier = Modifier.width(gap))
        } else if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(iconSize),
            )
            Spacer(modifier = Modifier.width(gap))
        }

        KomiText(
            text = label,
            role = labelRole,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        if (trailingIcon != null && !loading) {
            Spacer(modifier = Modifier.width(gap))
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                modifier = Modifier.size(iconSize),
            )
        }
    }
}

private data class ButtonMetrics(
    val height: Dp,
    val hPadding: Dp,
    val labelRole: KomiTextRole,
    val icon: Dp,
    val gap: Dp,
    val shadow: Dp,
    val border: Dp,
)

private fun buttonMetrics(size: KomiButtonSize): ButtonMetrics =
    when (size) {
        KomiButtonSize.Sm -> ButtonMetrics(34.dp, 14.dp, KomiTextRole.LabelSmall, 15.dp, 7.dp, 3.dp, 1.5.dp)
        KomiButtonSize.Md -> ButtonMetrics(44.dp, 20.dp, KomiTextRole.Label, 18.dp, 9.dp, 4.dp, 2.5.dp)
        KomiButtonSize.Lg -> ButtonMetrics(54.dp, 26.dp, KomiTextRole.LabelLarge, 22.dp, 10.dp, 5.dp, 3.dp)
    }

@Composable
private fun PreviewButtonStack() {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        KomiButton(
            onClick = {},
            label = "Install",
            variant = KomiButtonVariant.Primary,
            leadingIcon = Icons.Default.Add,
        )
        KomiButton(
            onClick = {},
            label = "Star",
            variant = KomiButtonVariant.Tonal,
            leadingIcon = Icons.Default.Check,
        )
        KomiButton(onClick = {}, label = "Share", variant = KomiButtonVariant.Outline)
        KomiButton(onClick = {}, label = "Details", variant = KomiButtonVariant.Text)
        KomiButton(onClick = {}, label = "Uninstall", variant = KomiButtonVariant.Destructive)
        KomiButton(
            onClick = {},
            label = "Install Now · 78 MB",
            variant = KomiButtonVariant.Primary,
            fullWidth = true,
            leadingIcon = Icons.Default.Add,
        )
    }
}

@Preview
@Composable
private fun KomiButtonClassicPreview() {
    PersonalityPreview(classicPersonality()) { PreviewButtonStack() }
}

@Preview
@Composable
private fun KomiButtonClassicDarkPreview() {
    PersonalityPreview(classicPersonality(dark = true)) { PreviewButtonStack() }
}
