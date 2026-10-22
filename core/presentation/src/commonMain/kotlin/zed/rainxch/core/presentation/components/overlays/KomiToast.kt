package zed.rainxch.core.presentation.components.overlays

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import zed.rainxch.core.presentation.components.text.KomiText
import zed.rainxch.core.presentation.components.text.KomiTextRole
import zed.rainxch.core.presentation.locals.LocalPersonality
import zed.rainxch.core.presentation.locals.LocalStatusColors
import zed.rainxch.core.presentation.personality.ClassicPersonality
import zed.rainxch.core.presentation.personality.classicPersonality
import zed.rainxch.core.presentation.personality.model.MotionLevel
import zed.rainxch.core.presentation.personality.utils.PersonalityPreview
import zed.rainxch.core.presentation.status.StatusColors

private val ToastMaxWidth = 440.dp

@Composable
fun KomiToastHost(
    state: KomiToastState,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            state.toasts.forEach { data ->
                key(data.id) {
                    KomiToastItem(data = data, onDismiss = state::dismiss)
                }
            }
        }
    }
}

@Composable
private fun KomiToastItem(
    data: KomiToastData,
    onDismiss: (Long) -> Unit,
) {
    val personality = LocalPersonality.current
    val motionOff = personality.motion.level == MotionLevel.OFF
    val durationMillis = data.durationMillis ?: if (data.actionLabel != null) 6000L else 4000L

    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val progress = remember { Animatable(1f) }

    var leaving by remember { mutableStateOf(false) }
    val visibleState = remember { MutableTransitionState(false) }
    LaunchedEffect(Unit) { visibleState.targetState = true }

    val beginClose =
        remember {
            {
                leaving = true
                visibleState.targetState = false
            }
        }

    LaunchedEffect(leaving, visibleState.isIdle) {
        if (leaving && visibleState.isIdle && !visibleState.currentState) onDismiss(data.id)
    }

    LaunchedEffect(hovered, data.persistent, visibleState.targetState) {
        if (data.persistent || !visibleState.targetState) return@LaunchedEffect
        if (hovered) {
            progress.stop()
        } else {
            val remaining = (progress.value * durationMillis).toInt().coerceAtLeast(0)
            progress.animateTo(targetValue = 0f, animationSpec = tween(remaining, easing = LinearEasing))
            beginClose()
        }
    }

    AnimatedVisibility(
        visibleState = visibleState,
        enter =
            if (motionOff) {
                EnterTransition.None
            } else {
                slideInVertically(personality.motion.defaultSpatial()) { it } +
                    fadeIn() +
                    scaleIn(initialScale = 0.96f)
            },
        exit = if (motionOff) ExitTransition.None else slideOutVertically { it / 3 } + fadeOut(),
    ) {
        KomiToastSurface(
            data = data,
            interaction = interaction,
            onClose = beginClose,
        )
    }
}

@Composable
private fun KomiToastSurface(
    data: KomiToastData,
    interaction: MutableInteractionSource,
    onClose: () -> Unit,
) {
    when (val personality = LocalPersonality.current) {
        is ClassicPersonality -> ClassicToastPanel(data, interaction, onClose)
    }
}

@Composable
private fun ClassicToastPanel(
    data: KomiToastData,
    interaction: MutableInteractionSource,
    onClose: () -> Unit,
) {
    val status = LocalStatusColors.current
    val hasAction = data.actionLabel != null
    val showX = data.dismissible ?: !hasAction
    val toneTint = toneTintOrNull(data.tone, status)

    Snackbar(
        modifier = Modifier.hoverable(interaction).widthIn(max = ToastMaxWidth),
        action =
            if (hasAction) {
                {
                    TextButton(onClick = {
                        data.onAction?.invoke()
                        onClose()
                    }) { Text(data.actionLabel) }
                }
            } else {
                null
            },
        dismissAction =
            if (showX) {
                {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Dismiss")
                    }
                }
            } else {
                null
            },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (toneTint != null) {
                Icon(
                    imageVector = toneIcon(data.tone),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = toneTint,
                )
            }
            Text(text = data.message)
        }
    }
}

private fun toneTintOrNull(
    tone: KomiToastTone,
    status: StatusColors,
): Color? =
    when (tone) {
        KomiToastTone.Default -> null
        KomiToastTone.Success -> status.statusReady
        KomiToastTone.Warning -> status.statusWarning
        KomiToastTone.Danger -> status.statusError
        KomiToastTone.Info -> null
    }

private fun toneIcon(tone: KomiToastTone): ImageVector =
    when (tone) {
        KomiToastTone.Success -> Icons.Default.Check
        KomiToastTone.Warning -> Icons.Default.Warning
        KomiToastTone.Danger -> Icons.Default.Close
        KomiToastTone.Info -> Icons.Default.Info
        KomiToastTone.Default -> Icons.Default.Info
    }

@Composable
private fun PreviewToastStack() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        val interaction = remember { MutableInteractionSource() }
        KomiToastSurface(
            data = KomiToastData(id = 1, message = "Link copied to clipboard."),
            interaction = interaction,
            onClose = {},
        )
        KomiToastSurface(
            data = KomiToastData(id = 2, message = "immich v2.7.5 installed.", tone = KomiToastTone.Success),
            interaction = interaction,
            onClose = {},
        )
        KomiToastSurface(
            data =
                KomiToastData(
                    id = 3,
                    message = "Update available for jellyfin.",
                    tone = KomiToastTone.Info,
                    actionLabel = "Update",
                    onAction = {},
                ),
            interaction = interaction,
            onClose = {},
        )
        KomiToastSurface(
            data = KomiToastData(id = 4, message = "Download failed.", tone = KomiToastTone.Danger, actionLabel = "Retry", onAction = {}),
            interaction = interaction,
            onClose = {},
        )
    }
}

@Preview
@Composable
private fun KomiToastClassicPreview() {
    PersonalityPreview(classicPersonality()) { PreviewToastStack() }
}
