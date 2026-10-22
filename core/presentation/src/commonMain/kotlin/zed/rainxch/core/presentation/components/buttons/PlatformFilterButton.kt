package zed.rainxch.core.presentation.components.buttons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import zed.rainxch.core.domain.model.repository.DiscoveryPlatform
import zed.rainxch.core.presentation.utils.toIcon
import zed.rainxch.core.presentation.utils.toLabel
import zed.rainxch.githubstore.core.presentation.res.Res
import zed.rainxch.githubstore.core.presentation.res.feed_platform_all

// The one control that switches the browsing platform: it names the current choice and opens the
// picker. The feed and the charts bar both use it so the two screens keep the same actions.
@Composable
fun PlatformFilterButton(
    platform: DiscoveryPlatform,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: KomiButtonSize = KomiButtonSize.Sm,
) {
    val all = platform == DiscoveryPlatform.All
    KomiButton(
        onClick = onClick,
        label = if (all) stringResource(Res.string.feed_platform_all) else platform.toLabel(),
        variant = KomiButtonVariant.Primary,
        size = size,
        leadingIcon = if (all) null else platform.toIcon(),
        trailingIcon = Icons.Rounded.KeyboardArrowDown,
        modifier = modifier,
    )
}
