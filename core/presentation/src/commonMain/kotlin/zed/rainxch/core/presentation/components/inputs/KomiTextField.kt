package zed.rainxch.core.presentation.components.inputs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import zed.rainxch.core.presentation.components.text.KomiText
import zed.rainxch.core.presentation.components.text.KomiTextRole
import zed.rainxch.core.presentation.locals.LocalPersonality
import zed.rainxch.core.presentation.personality.ClassicPersonality
import zed.rainxch.core.presentation.personality.classicPersonality
import zed.rainxch.core.presentation.personality.model.PersonalityColors
import zed.rainxch.core.presentation.personality.model.PersonalityType
import zed.rainxch.core.presentation.personality.utils.PersonalityPreview
import zed.rainxch.core.presentation.spacing.Spacing
import zed.rainxch.githubstore.core.presentation.res.Res
import zed.rainxch.githubstore.core.presentation.res.clear
import zed.rainxch.githubstore.core.presentation.res.hide_password
import zed.rainxch.githubstore.core.presentation.res.show_password

@Composable
fun KomiTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    helper: String? = null,
    error: String? = null,
    placeholder: String? = null,
    leadingIcon: ImageVector? = null,
    trailing: @Composable (() -> Unit)? = null,
    clearable: Boolean = false,
    onClear: (() -> Unit)? = null,
    multiline: Boolean = false,
    rows: Int = 4,
    required: Boolean = false,
    enabled: Boolean = true,
    password: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    onCommit: (() -> Unit)? = null,
) {
    // onClear replaces the plain clear for a screen whose clear does more than empty the text.
    val clear: (() -> Unit)? = onClear ?: if (clearable) ({ onValueChange("") }) else null
    when (val personality = LocalPersonality.current) {
        is ClassicPersonality -> {
            ClassicTextField(
                personality = personality,
                value = value,
                onValueChange = onValueChange,
                modifier = modifier,
                label = label,
                helper = helper,
                error = error,
                placeholder = placeholder,
                leadingIcon = leadingIcon,
                trailing = trailing,
                onClear = clear,
                multiline = multiline,
                rows = rows,
                required = required,
                enabled = enabled,
                password = password,
                keyboardType = keyboardType,
                onCommit = onCommit,
            )
        }
    }
}

@Composable
private fun PreviewFields() {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.lg)) {
        KomiTextField(
            value = "",
            onValueChange = {},
            label = "Search",
            leadingIcon = Icons.Default.Search,
            clearable = true,
            placeholder = "Search apps…",
            helper = "Press Enter to search.",
            onCommit = {},
        )
        KomiTextField(
            value = "immich-app",
            onValueChange = {},
            label = "Owner",
            required = true,
            helper = "GitHub org or user.",
        )
        KomiTextField(
            value = "not-an-email",
            onValueChange = {},
            label = "Email",
            leadingIcon = Icons.Default.MailOutline,
            error = "That doesn't look like an email.",
        )
        KomiTextField(value = "Self-hosted photo backup.", onValueChange = {
        }, label = "Description", multiline = true, rows = 3, helper = "Shown on the app card.")
        KomiTextField(
            value = "locked",
            onValueChange = {},
            label = "Disabled",
            enabled = false,
            helper = "Read-only.",
        )
    }
}

@Preview
@Composable
private fun KomiTextFieldClassicPreview() {
    PersonalityPreview(classicPersonality()) { PreviewFields() }
}

@Preview
@Composable
private fun KomiTextFieldClassicDarkPreview() {
    PersonalityPreview(classicPersonality(dark = true)) { PreviewFields() }
}
