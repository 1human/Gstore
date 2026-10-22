package zed.rainxch.core.presentation.components.chips

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
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
import zed.rainxch.core.presentation.personality.model.PersonalityColors
import zed.rainxch.core.presentation.personality.utils.PersonalityPreview
import zed.rainxch.core.presentation.spacing.Spacing
import androidx.compose.material3.FilterChip as MaterialFilterChip

@Composable
fun KomiChip(
    label: String,
    modifier: Modifier = Modifier,
    kind: KomiChipKind = KomiChipKind.Info,
    selected: Boolean = false,
    leadingIcon: ImageVector? = null,
    leadingContent: (@Composable () -> Unit)? = null,
    count: Int? = null,
    onClick: (() -> Unit)? = null,
    onRemove: (() -> Unit)? = null,
) {
    when (LocalPersonality.current) {
        is ClassicPersonality -> {
            ClassicChip(
                label = label,
                modifier = modifier,
                kind = kind,
                selected = selected,
                leadingIcon = leadingIcon,
                leadingContent = leadingContent,
                count = count,
                onClick = onClick,
                onRemove = onRemove,
            )
        }
    }
}

@Composable
private fun ClassicChip(
    label: String,
    modifier: Modifier,
    kind: KomiChipKind,
    selected: Boolean,
    leadingIcon: ImageVector?,
    leadingContent: (@Composable () -> Unit)?,
    count: Int?,
    onClick: (() -> Unit)?,
    onRemove: (() -> Unit)?,
) {
    val text = if (count != null) "$label  $count" else label
    val leading: @Composable (() -> Unit)? =
        leadingContent
            ?: leadingIcon?.let {
                { Icon(imageVector = it, contentDescription = null, modifier = Modifier.size(18.dp)) }
            }

    when (kind) {
        KomiChipKind.Info -> {
            AssistChip(
                onClick = onClick ?: {},
                label = { Text(text) },
                modifier = modifier,
                leadingIcon = leading,
            )
        }

        KomiChipKind.Filter -> {
            MaterialFilterChip(
                selected = selected,
                onClick = onClick ?: {},
                label = { Text(text) },
                modifier = modifier,
                leadingIcon = leading,
            )
        }

        KomiChipKind.Input -> {
            InputChip(
                selected = selected,
                onClick = onClick ?: {},
                label = { Text(text) },
                modifier = modifier,
                leadingIcon = leading,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove",
                        modifier = Modifier.size(18.dp).clickable(onClick = onRemove ?: {}),
                    )
                },
            )
        }
    }
}


@Composable
private fun PreviewInfoRow() {
    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        KomiChip("Android", kind = KomiChipKind.Info, leadingIcon = Icons.Default.Place)
        KomiChip("Windows", kind = KomiChipKind.Info, leadingIcon = Icons.Default.Place)
        KomiChip("#self-hosted", kind = KomiChipKind.Info)
        KomiChip("AGPL-3.0", kind = KomiChipKind.Info)
    }
}

@Composable
private fun PreviewFilterRow() {
    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        listOf("all", "media", "privacy", "ai", "dev tools").forEach { c ->
            KomiChip(c, kind = KomiChipKind.Filter, selected = c == "media", onClick = {})
        }
    }
}

@Composable
private fun PreviewCountInputRow() {
    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm), verticalAlignment = Alignment.CenterVertically) {
        KomiChip("Photos", kind = KomiChipKind.Filter, selected = true, count = 102, onClick = {})
        KomiChip("Media", kind = KomiChipKind.Filter, count = 38, onClick = {})
        KomiChip("self-hosted", kind = KomiChipKind.Input, onRemove = {})
    }
}

@Preview
@Composable
private fun KomiChipClassicPreview() {
    PersonalityPreview(classicPersonality()) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
            PreviewInfoRow()
            PreviewFilterRow()
            PreviewCountInputRow()
        }
    }
}
