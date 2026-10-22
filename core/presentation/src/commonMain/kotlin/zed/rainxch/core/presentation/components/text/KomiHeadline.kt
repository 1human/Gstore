package zed.rainxch.core.presentation.components.text

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import zed.rainxch.core.presentation.locals.LocalPersonality
import zed.rainxch.core.presentation.personality.ClassicPersonality
import zed.rainxch.core.presentation.personality.classicPersonality
import zed.rainxch.core.presentation.personality.utils.PersonalityPreview

@Composable
fun KomiHeadline(
    text: String,
    modifier: Modifier = Modifier,
    role: KomiTextRole = KomiTextRole.SectionTitle,
    color: Color = Color.Unspecified,
    maxLines: Int = 1,
) {
    when (LocalPersonality.current) {
        is ClassicPersonality -> {
            KomiText(
                text = text,
                modifier = modifier,
                role = role,
                color = color,
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview
@Composable
private fun KomiHeadlineClassicPreview() {
    PersonalityPreview(classicPersonality()) {
        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            KomiHeadline("Discover", role = KomiTextRole.Display)
            KomiHeadline("Trending Now")
        }
    }
}
