package zed.rainxch.core.presentation.components.progress

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.LoadingIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import zed.rainxch.core.presentation.locals.LocalPersonality

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun KomiCircularProgress(
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
) {
    val colors = LocalPersonality.current.colors
    val resolved = if (color != Color.Unspecified) color else colors.primary
    CircularWavyProgressIndicator(
        modifier = modifier,
        color = resolved,
    )
}

@Composable
fun KomiCircularProgress(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
) {
    val colors = LocalPersonality.current.colors
    val resolved = if (color != Color.Unspecified) color else colors.primary
    CircularProgressIndicator(
        progress = progress,
        modifier = modifier,
        color = resolved,
        strokeCap = StrokeCap.Round,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun KomiLinearProgress(
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
) {
    val colors = LocalPersonality.current.colors
    val resolved = if (color != Color.Unspecified) color else colors.primary
    LinearWavyProgressIndicator(
        modifier = modifier,
        color = resolved,
    )
}

@Composable
fun KomiLinearProgress(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
) {
    val colors = LocalPersonality.current.colors
    val resolved = if (color != Color.Unspecified) color else colors.primary
    LinearProgressIndicator(
        progress = progress,
        modifier = modifier,
        color = resolved,
        trackColor = colors.surfaceVariant,
        strokeCap = StrokeCap.Round,
    )
}

// A screen or panel waiting a few seconds for its content. Waits inside buttons and rows, and
// long ones (sign-in, scans), stay on KomiCircularProgress.
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun KomiLoadingIndicator(modifier: Modifier = Modifier) {
    val colors = LocalPersonality.current.colors
    LoadingIndicator(modifier = modifier, color = colors.primary)
}
