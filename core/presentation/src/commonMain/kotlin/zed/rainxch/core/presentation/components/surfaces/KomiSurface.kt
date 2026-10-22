package zed.rainxch.core.presentation.components.surfaces

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import zed.rainxch.core.presentation.components.text.KomiText
import zed.rainxch.core.presentation.components.text.KomiTextRole
import zed.rainxch.core.presentation.locals.LocalPersonality
import zed.rainxch.core.presentation.personality.ClassicPersonality
import zed.rainxch.core.presentation.personality.classicPersonality
import zed.rainxch.core.presentation.personality.utils.PersonalityPreview
import zed.rainxch.core.presentation.spacing.Spacing

@Composable
fun KomiSurface(
    modifier: Modifier = Modifier,
    elevation: KomiSurfaceElevation = KomiSurfaceElevation.Card,
    paper: KomiSurfacePaper = KomiSurfacePaper.Surface,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable () -> Unit,
) {
    when (val personality = LocalPersonality.current) {
        is ClassicPersonality -> {
            ClassicSurface(
                personality = personality,
                modifier = modifier,
                elevation = elevation,
                paper = paper,
                onClick = onClick,
                onLongClick = onLongClick,
                contentPadding = contentPadding,
                content = content,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ClassicSurface(
    personality: ClassicPersonality,
    modifier: Modifier = Modifier,
    elevation: KomiSurfaceElevation,
    paper: KomiSurfacePaper,
    onClick: (() -> Unit)?,
    onLongClick: (() -> Unit)?,
    contentPadding: PaddingValues,
    content: @Composable () -> Unit,
) {
    val colors = personality.colors
    val shape = remember(personality.shape.corner) { RoundedCornerShape(personality.shape.corner) }
    // Tonal surfaces: the container color steps up with elevation instead of drawing an outline.
    // A background-colored surface keeps the outline, since it would vanish into the page.
    val fill =
        when (paper) {
            KomiSurfacePaper.Background -> {
                colors.background
            }

            KomiSurfacePaper.Surface -> {
                when (elevation) {
                    KomiSurfaceElevation.Flat -> colors.surface
                    KomiSurfaceElevation.Card -> colors.surfaceContainer
                    KomiSurfaceElevation.Raised, KomiSurfaceElevation.Modal -> colors.surfaceContainerHigh
                }
            }
        }
    val border =
        if (paper == KomiSurfacePaper.Background) BorderStroke(personality.shape.borderPanel, colors.outlineVariant) else null
    val shadowElevation =
        when (elevation) {
            KomiSurfaceElevation.Flat, KomiSurfaceElevation.Card -> 0.dp
            KomiSurfaceElevation.Raised -> 3.dp
            KomiSurfaceElevation.Modal -> 6.dp
        }

    val clickModifier =
        if (onClick != null || onLongClick != null) {
            Modifier.combinedClickable(onClick = onClick ?: {}, onLongClick = onLongClick)
        } else {
            Modifier
        }
    Surface(
        modifier = modifier,
        shape = shape,
        color = fill,
        contentColor = colors.onSurface,
        border = border,
        shadowElevation = shadowElevation,
    ) {
        Box(modifier = Modifier.then(clickModifier).padding(contentPadding)) { content() }
    }
}

@Composable
private fun PreviewPanelBody() {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        KomiText("Ollama", role = KomiTextRole.CardTitle)
        KomiText("Run large language models locally.", role = KomiTextRole.Body)
        KomiText("v0.5.4 · 174M", role = KomiTextRole.Mono)
    }
}

@Preview
@Composable
private fun KomiSurfaceClassicPreview() {
    PersonalityPreview(classicPersonality()) {
        KomiSurface(onClick = {}, contentPadding = PaddingValues(Spacing.lg)) { PreviewPanelBody() }
    }
}

@Preview
@Composable
private fun KomiSurfaceClassicDarkPreview() {
    PersonalityPreview(classicPersonality(dark = true)) {
        KomiSurface(
            elevation = KomiSurfaceElevation.Modal,
            contentPadding = PaddingValues(Spacing.lg),
        ) {
            PreviewPanelBody()
        }
    }
}
