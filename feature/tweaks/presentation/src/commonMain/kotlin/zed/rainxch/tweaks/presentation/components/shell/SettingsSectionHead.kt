package zed.rainxch.tweaks.presentation.components.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import zed.rainxch.core.presentation.components.text.KomiText
import zed.rainxch.core.presentation.components.text.KomiTextRole
import zed.rainxch.core.presentation.locals.LocalPersonality

@Composable
fun SettingsSectionHead(
    label: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalPersonality.current.colors
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(top = 14.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        KomiText(
            text = label,
            role = KomiTextRole.SectionTitle,
            color = colors.onSurface,
        )

        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(colors.divider),
        )
    }
}
