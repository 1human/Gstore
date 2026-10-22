package zed.rainxch.tweaks.presentation.components.desktop

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import zed.rainxch.core.presentation.components.buttons.KomiIconButton
import zed.rainxch.core.presentation.components.text.KomiText
import zed.rainxch.core.presentation.components.text.KomiTextRole
import zed.rainxch.core.presentation.locals.LocalPersonality
import zed.rainxch.githubstore.core.presentation.res.Res
import zed.rainxch.githubstore.core.presentation.res.navigate_back
import zed.rainxch.githubstore.core.presentation.res.section_app_block
import zed.rainxch.githubstore.core.presentation.res.section_connectivity
import zed.rainxch.githubstore.core.presentation.res.section_privacy_and_data
import zed.rainxch.githubstore.core.presentation.res.tweaks_title
import zed.rainxch.tweaks.presentation.TweaksAction
import zed.rainxch.tweaks.presentation.TweaksState
import zed.rainxch.tweaks.presentation.components.sections.appSection
import zed.rainxch.tweaks.presentation.components.sections.connectivitySection
import zed.rainxch.tweaks.presentation.components.sections.privacySection

enum class DesktopSection {
    CONNECTIVITY,
    PRIVACY,
    APP,
}

@Composable
private fun DesktopSection.label(): String =
    when (this) {
        DesktopSection.CONNECTIVITY -> stringResource(Res.string.section_connectivity)
        DesktopSection.PRIVACY -> stringResource(Res.string.section_privacy_and_data)
        DesktopSection.APP -> stringResource(Res.string.section_app_block)
    }

@Composable
fun TweaksDesktopContent(
    state: TweaksState,
    onAction: (TweaksAction) -> Unit,
    currentLanguageLabel: String,
    onOpenLanguage: () -> Unit,
    onOpenFeedback: () -> Unit,
    onNavigateToMirrorPicker: () -> Unit,
    onNavigateToHiddenRepositories: () -> Unit,
    onNavigateToHostTokens: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalPersonality.current.colors

    Row(modifier = modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
                    .width(230.dp)
                    .fillMaxHeight()
                    .background(colors.surface)
                    .drawBehind {
                        val stroke = 1.dp.toPx()
                        drawLine(
                            color = colors.outlineVariant,
                            start = Offset(size.width - stroke / 2f, 0f),
                            end = Offset(size.width - stroke / 2f, size.height),
                            strokeWidth = stroke,
                        )
                    }.padding(horizontal = 12.dp, vertical = 18.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                KomiIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(Res.string.navigate_back),
                    onClick = onNavigateBack,
                )
                KomiText(
                    text = stringResource(Res.string.tweaks_title),
                    role = KomiTextRole.ScreenTitle,
                    color = colors.onSurface,
                    modifier = Modifier.padding(start = 6.dp),
                )
            }
            DesktopSection.entries.forEach { entry ->
                NavRow(
                    label = entry.label(),
                    active = state.desktopSection == entry,
                    onClick = { onAction(TweaksAction.OnDesktopSectionSelected(entry)) },
                )
            }
        }

        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
        ) {
            Column(
                modifier =
                    Modifier
                        .widthIn(max = 680.dp)
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(horizontal = 28.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                when (state.desktopSection) {
                    DesktopSection.CONNECTIVITY -> {
                        connectivitySection(
                            state = state,
                            onAction = onAction,
                            onNavigateToMirrorPicker = onNavigateToMirrorPicker,
                        )
                    }

                    DesktopSection.PRIVACY -> {
                        privacySection(
                            state = state,
                            onAction = onAction,
                            onNavigateToHiddenRepositories = onNavigateToHiddenRepositories,
                            onNavigateToHostTokens = onNavigateToHostTokens,
                        )
                    }

                    DesktopSection.APP -> {
                        appSection(
                            onOpenFeedback = onOpenFeedback,
                            currentLanguageLabel = currentLanguageLabel,
                            onOpenLanguage = onOpenLanguage,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NavRow(
    label: String,
    active: Boolean,
    onClick: () -> Unit,
) {
    val colors = LocalPersonality.current.colors
    val shape = RoundedCornerShape(LocalPersonality.current.shape.cornerSmall)
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp)
                .clip(shape)
                .then(if (active) Modifier.background(colors.primary, shape) else Modifier)
                .height(40.dp)
                .clickable(onClick = onClick)
                .padding(horizontal = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        KomiText(
            text = label,
            role = KomiTextRole.Label,
            color = if (active) colors.onPrimary else colors.onSurface,
            modifier = Modifier.weight(1f),
        )
    }
}
