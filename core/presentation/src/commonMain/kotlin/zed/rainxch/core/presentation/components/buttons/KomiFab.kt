package zed.rainxch.core.presentation.components.buttons

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import zed.rainxch.core.presentation.components.bars.LocalInBar
import zed.rainxch.core.presentation.components.icon.KomiIcon
import zed.rainxch.core.presentation.components.text.KomiText
import zed.rainxch.core.presentation.components.text.KomiTextRole
import zed.rainxch.core.presentation.locals.LocalPersonality

@Composable
fun KomiFab(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    label: String? = null,
) {
    val colors = LocalPersonality.current.colors
    if (label == null && LocalInBar.current) {
        KomiCookieButton(
            icon = icon,
            contentDescription = contentDescription,
            onClick = onClick,
            containerColor = colors.surfaceContainerHigh,
            contentColor = colors.onSurface,
            modifier = modifier,
        )
    } else if (label != null) {
        ExtendedFloatingActionButton(
            onClick = onClick,
            modifier = modifier,
            containerColor = colors.surfaceContainerHigh,
            contentColor = colors.onSurface,
            icon = { KomiIcon(icon, contentDescription, modifier = Modifier.size(20.dp)) },
            text = { KomiText(label, role = KomiTextRole.LabelLarge) },
        )
    } else {
        FloatingActionButton(
            onClick = onClick,
            modifier = modifier,
            containerColor = colors.surfaceContainerHigh,
            contentColor = colors.onSurface,
        ) {
            KomiIcon(icon, contentDescription, modifier = Modifier.size(24.dp))
        }
    }
}
