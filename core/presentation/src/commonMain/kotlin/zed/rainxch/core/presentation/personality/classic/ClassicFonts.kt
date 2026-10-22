package zed.rainxch.core.presentation.personality.classic

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import zed.rainxch.core.presentation.personality.model.PersonalityType

/**
 * Classic ships no font files: text renders with the platform's system face, and monospace
 * roles use the system monospace family. That keeps the APK small and gives every script
 * (Latin, CJK, Cyrillic, Arabic…) native glyph coverage and shaping.
 */
@Composable
fun PersonalityType.withClassicFonts(): PersonalityType =
    withFamilies(
        display = FontFamily.Default,
        body = FontFamily.Default,
        mono = FontFamily.Monospace,
    )
