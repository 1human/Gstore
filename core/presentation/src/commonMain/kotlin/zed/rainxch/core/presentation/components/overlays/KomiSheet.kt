package zed.rainxch.core.presentation.components.overlays

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import zed.rainxch.core.presentation.components.buttons.KomiButton
import zed.rainxch.core.presentation.components.buttons.KomiButtonVariant
import zed.rainxch.core.presentation.components.text.KomiText
import zed.rainxch.core.presentation.components.text.KomiTextRole
import zed.rainxch.core.presentation.layout.currentWindowSize
import zed.rainxch.core.presentation.layout.formFactorFor
import zed.rainxch.core.presentation.layout.isCompact
import zed.rainxch.core.presentation.locals.LocalPersonality
import zed.rainxch.core.presentation.personality.ClassicPersonality
import zed.rainxch.core.presentation.personality.classicPersonality
import zed.rainxch.core.presentation.personality.model.MotionLevel
import zed.rainxch.core.presentation.personality.utils.PersonalityPreview

@Composable
fun KomiSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    placement: KomiSheetPlacement = KomiSheetPlacement.Auto,
    title: String? = null,
    maxWidth: Dp = 520.dp,
    dismissible: Boolean = true,
    footer: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val windowSize = currentWindowSize()
    val screenHeight = windowSize.height
    val resolved =
        when (placement) {
            KomiSheetPlacement.Bottom -> {
                KomiSheetPlacement.Bottom
            }

            KomiSheetPlacement.Center -> {
                KomiSheetPlacement.Center
            }

            KomiSheetPlacement.Auto -> {
                if (formFactorFor(windowSize.width).isCompact) KomiSheetPlacement.Bottom else KomiSheetPlacement.Center
            }
        }

    when (LocalPersonality.current) {
        is ClassicPersonality -> {
            if (resolved == KomiSheetPlacement.Bottom) {
                ClassicBottomSheet(
                    onDismiss = onDismiss,
                    title = title,
                    footer = footer,
                    screenHeight = screenHeight,
                    modifier = modifier,
                    content = content,
                )
            } else {
                ClassicCenterDialog(
                    onDismiss = onDismiss,
                    title = title,
                    footer = footer,
                    maxWidth = maxWidth,
                    dismissible = dismissible,
                    screenHeight = screenHeight,
                    modifier = modifier,
                    content = content,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClassicBottomSheet(
    onDismiss: () -> Unit,
    title: String?,
    footer: (@Composable () -> Unit)?,
    screenHeight: Dp,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = LocalPersonality.current.colors
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        sheetState = sheetState,
        containerColor = colors.surface,
        contentColor = colors.onSurface,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .heightIn(max = screenHeight * 0.85f)
                    .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
        ) {
            if (title != null) {
                KomiText(
                    text = title,
                    role = KomiTextRole.CardTitle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(16.dp))
            }
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                content = content,
            )
            if (footer != null) {
                Spacer(Modifier.height(16.dp))
                footer()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClassicCenterDialog(
    onDismiss: () -> Unit,
    title: String?,
    footer: (@Composable () -> Unit)?,
    maxWidth: Dp,
    dismissible: Boolean,
    screenHeight: Dp,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = LocalPersonality.current.colors

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        properties =
            DialogProperties(
                dismissOnBackPress = dismissible,
                dismissOnClickOutside = dismissible,
            ),
    ) {
        Surface(
            modifier = Modifier.widthIn(max = maxWidth).heightIn(max = screenHeight * 0.9f),
            shape = RoundedCornerShape(LocalPersonality.current.shape.corner),
            color = colors.surfaceContainerHigh,
            contentColor = colors.onSurface,
            tonalElevation = 6.dp,
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                if (title != null) {
                    KomiText(
                        text = title,
                        role = KomiTextRole.CardTitle,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(16.dp))
                }
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false)
                            .verticalScroll(rememberScrollState()),
                    content = content,
                )
                if (footer != null) {
                    Spacer(Modifier.height(24.dp))
                    footer()
                }
            }
        }
    }
}

@Composable
private fun PreviewBody() {
    val colors = LocalPersonality.current.colors
    KomiText(
        text = "Remove immich from this device? Your library on the server is untouched.",
        role = KomiTextRole.Body,
        color = colors.onSurface,
    )
}

@Composable
private fun PreviewFooter() {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        KomiButton(
            onClick = {},
            label = "Cancel",
            variant = KomiButtonVariant.Outline,
            modifier = Modifier.weight(1f),
        )
        KomiButton(
            onClick = {},
            label = "Remove",
            variant = KomiButtonVariant.Destructive,
            modifier = Modifier.weight(1f),
        )
    }
}

@Preview
@Composable
private fun KomiSheetClassicPreview() {
    PersonalityPreview(classicPersonality()) {
        Box(Modifier.fillMaxWidth().padding(8.dp)) {
            KomiSheet(
                onDismiss = {},
                placement = KomiSheetPlacement.Center,
                title = "Uninstall?",
                footer = { PreviewFooter() },
            ) {
                PreviewBody()
            }
        }
    }
}
