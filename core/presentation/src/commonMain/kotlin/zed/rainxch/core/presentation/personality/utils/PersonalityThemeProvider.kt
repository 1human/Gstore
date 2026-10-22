package zed.rainxch.core.presentation.personality.utils

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import zed.rainxch.core.presentation.locals.LocalPersonality
import zed.rainxch.core.presentation.locals.LocalStatusColors
import zed.rainxch.core.presentation.personality.ClassicPersonality
import zed.rainxch.core.presentation.personality.classic.withClassicFonts
import zed.rainxch.core.presentation.status.statusColors

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PersonalityTheme(
    personality: ClassicPersonality,
    content: @Composable () -> Unit,
) {
    val resolved = personality.copy(type = personality.type.withClassicFonts())
    val typography = resolved.toMaterialTypography()
    CompositionLocalProvider(
        LocalPersonality provides resolved,
        LocalStatusColors provides statusColors(resolved.colors.isDark),
        LocalContentColor provides resolved.colors.onBackground,
    ) {
        MaterialExpressiveTheme(colorScheme = resolved.colorScheme, typography = typography, content = content)
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private fun ClassicPersonality.toMaterialTypography(): Typography {
    val roles = type
    val display = roles.display
    val headline = roles.screenTitle
    val title = roles.sectionTitle
    return Typography(
        displayLarge = display,
        displayMedium = display,
        displaySmall = display,
        headlineLarge = display,
        headlineMedium = headline,
        headlineSmall = headline,
        titleLarge = title,
        titleMedium = roles.cardTitle,
        titleSmall = roles.label,
        bodyLarge = roles.reading,
        bodyMedium = roles.body,
        bodySmall = roles.caption,
        labelLarge = roles.label,
        labelMedium = roles.labelSmall,
        labelSmall = roles.overline,
        displayLargeEmphasized = emphasized(display),
        displayMediumEmphasized = emphasized(display),
        displaySmallEmphasized = emphasized(display),
        headlineLargeEmphasized = emphasized(display),
        headlineMediumEmphasized = emphasized(headline),
        headlineSmallEmphasized = emphasized(headline),
        titleLargeEmphasized = emphasized(title),
        titleMediumEmphasized = emphasized(roles.cardTitle),
        titleSmallEmphasized = emphasized(roles.label),
        bodyLargeEmphasized = emphasized(roles.reading),
        bodyMediumEmphasized = emphasized(roles.body),
        bodySmallEmphasized = emphasized(roles.caption),
        labelLargeEmphasized = emphasized(roles.label),
        labelMediumEmphasized = emphasized(roles.labelSmall),
        labelSmallEmphasized = emphasized(roles.overline),
    )
}

private fun emphasized(style: TextStyle): TextStyle {
    val step = ClassicWeights.indexOf(style.fontWeight)
    return if (step in 0 until ClassicWeights.lastIndex) style.copy(fontWeight = ClassicWeights[step + 1]) else style
}

private val ClassicWeights = listOf(FontWeight.Medium, FontWeight.Bold, FontWeight.ExtraBold)
