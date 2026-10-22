package zed.rainxch.apps.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import zed.rainxch.apps.presentation.AppsAction
import zed.rainxch.apps.presentation.model.InstalledAppUi
import zed.rainxch.core.presentation.components.buttons.KomiButton
import zed.rainxch.core.presentation.components.buttons.KomiButtonVariant
import zed.rainxch.core.presentation.components.overlays.KomiSheet
import zed.rainxch.core.presentation.components.overlays.KomiSheetPlacement
import zed.rainxch.core.presentation.components.text.KomiText
import zed.rainxch.core.presentation.components.text.KomiTextRole
import zed.rainxch.githubstore.core.presentation.res.Res
import zed.rainxch.githubstore.core.presentation.res.cancel
import zed.rainxch.githubstore.core.presentation.res.details_unlink_external_app_dialog_body
import zed.rainxch.githubstore.core.presentation.res.details_unlink_external_app_dialog_confirm
import zed.rainxch.githubstore.core.presentation.res.details_unlink_external_app_dialog_title

@Composable
fun PendingUnlinkSheet(
    app: InstalledAppUi,
    onAction: (AppsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    KomiSheet(
        onDismiss = {
            onAction(AppsAction.OnDismissUnlinkDialog)
        },
        modifier = modifier,
        placement = KomiSheetPlacement.Center,
        title = stringResource(Res.string.details_unlink_external_app_dialog_title),
        footer = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                KomiButton(
                    onClick = {
                        onAction(AppsAction.OnDismissUnlinkDialog)
                    },
                    label = stringResource(Res.string.cancel),
                    variant = KomiButtonVariant.Text,
                )
                KomiButton(
                    onClick = {
                        onAction(AppsAction.OnUnlinkConfirmed(app))
                    },
                    label = stringResource(Res.string.details_unlink_external_app_dialog_confirm),
                    variant = KomiButtonVariant.Destructive,
                )
            }
        },
    ) {
        KomiText(
            text = stringResource(Res.string.details_unlink_external_app_dialog_body, app.appName),
            role = KomiTextRole.Body,
        )
    }
}
