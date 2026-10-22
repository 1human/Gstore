package zed.rainxch.core.presentation.utils

import androidx.compose.ui.input.pointer.PointerIcon
import java.awt.Cursor

// E_RESIZE_CURSOR is the double-headed left-right arrow Windows uses on column splitters.
actual val horizontalResizePointerIcon: PointerIcon =
    PointerIcon(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR))
