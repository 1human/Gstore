package zed.rainxch.core.presentation.personality.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import zed.rainxch.core.presentation.locals.LocalPersonality
import zed.rainxch.core.presentation.personality.ClassicPersonality
import zed.rainxch.core.presentation.personality.classicPersonality

@Composable
fun PersonalityPreview(
    personality: ClassicPersonality = classicPersonality(),
    content: @Composable () -> Unit,
) {
    PersonalityTheme(personality) {
        Box(
            modifier =
                Modifier
                    .background(LocalPersonality.current.colors.background)
                    .padding(16.dp),
        ) {
            content()
        }
    }
}
