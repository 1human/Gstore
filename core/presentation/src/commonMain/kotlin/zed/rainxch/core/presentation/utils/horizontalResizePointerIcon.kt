package zed.rainxch.core.presentation.utils

import androidx.compose.ui.input.pointer.PointerIcon

// The split handle between two panes should show the platform's own horizontal resize cursor
// rather than a generic crosshair.
expect val horizontalResizePointerIcon: PointerIcon
