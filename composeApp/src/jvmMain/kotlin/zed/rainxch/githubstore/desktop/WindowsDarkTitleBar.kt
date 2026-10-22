package zed.rainxch.githubstore.desktop

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.ptr.IntByReference
import com.sun.jna.win32.StdCallLibrary
import zed.rainxch.core.domain.system.DesktopOs
import java.awt.Window

private interface DwmApi : StdCallLibrary {
    fun DwmSetWindowAttribute(
        hwnd: WinDef.HWND,
        attribute: Int,
        value: WinDef.BOOLByReference,
        size: Int,
    ): Int

    fun DwmSetWindowAttribute(
        hwnd: WinDef.HWND,
        attribute: Int,
        value: IntByReference,
        size: Int,
    ): Int

    companion object {
        val INSTANCE: DwmApi? =
            runCatching {
                Native.load("dwmapi", DwmApi::class.java)
            }.getOrNull()
    }
}

private const val DWMWA_USE_IMMERSIVE_DARK_MODE = 20
private const val DWMWA_USE_IMMERSIVE_DARK_MODE_PRE_20H1 = 19
private const val DWMWA_BORDER_COLOR = 34
private const val DWMWA_CAPTION_COLOR = 35
private const val DWMWA_TEXT_COLOR = 36
private const val DWMWA_COLOR_DEFAULT = -1
private const val FALLBACK_CAPTION_COLORREF = 0x001E1E1E
private const val S_OK = 0
private const val CHANNEL_MASK = 0xFF
private const val RED_SHIFT = 16
private const val GREEN_SHIFT = 8

/**
 * DWM wants a Win32 COLORREF (0x00BBGGRR): the app paints in ARGB, so the channels
 * have to be flipped or the caption comes out with red and blue swapped.
 */
fun Color.toTitleBarColorRef(): Int {
    val argb = toArgb()
    val red = (argb shr RED_SHIFT) and CHANNEL_MASK
    val green = (argb shr GREEN_SHIFT) and CHANNEL_MASK
    val blue = argb and CHANNEL_MASK
    return (blue shl RED_SHIFT) or (green shl GREEN_SHIFT) or red
}

private val isWindows: Boolean
    get() = DesktopOs.isWindows

private val isMac: Boolean
    get() = DesktopOs.isMac

fun installMacosSystemAppearance() {
    if (!isMac) return
    if (System.getProperty("apple.awt.application.appearance") == null) {
        System.setProperty("apple.awt.application.appearance", "system")
    }
}

fun applyMacosWindowAppearance(
    window: Window,
    isDark: Boolean,
) {
    if (!isMac) return
    val frame = window as? javax.swing.JFrame ?: return
    val key = "apple.awt.windowAppearance"
    val value = if (isDark) "NSAppearanceNameDarkAqua" else "NSAppearanceNameAqua"
    runCatching { frame.rootPane.putClientProperty(key, value) }
}

fun applyWindowsImmersiveDarkMode(
    window: Window,
    isDark: Boolean,
    captionColorRef: Int? = null,
    textColorRef: Int? = null,
) {
    val dwm = if (isWindows) DwmApi.INSTANCE else null
    val hwnd =
        dwm?.let {
            runCatching { WinDef.HWND(Pointer(Native.getWindowID(window))) }.getOrNull()
        }
    if (dwm == null || hwnd == null) return
    val flag = WinDef.BOOLByReference(WinDef.BOOL(isDark))
    val hr =
        runCatching {
            dwm.DwmSetWindowAttribute(hwnd, DWMWA_USE_IMMERSIVE_DARK_MODE, flag, 4)
        }.getOrElse { -1 }

    if (hr != S_OK) {
        runCatching {
            dwm.DwmSetWindowAttribute(hwnd, DWMWA_USE_IMMERSIVE_DARK_MODE_PRE_20H1, flag, 4)
        }
    }

    val colorRef = IntByReference(captionColorRef ?: if (isDark) FALLBACK_CAPTION_COLORREF else DWMWA_COLOR_DEFAULT)
    runCatching { dwm.DwmSetWindowAttribute(hwnd, DWMWA_CAPTION_COLOR, colorRef, 4) }
    runCatching { dwm.DwmSetWindowAttribute(hwnd, DWMWA_BORDER_COLOR, colorRef, 4) }
    textColorRef?.let { text ->
        runCatching { dwm.DwmSetWindowAttribute(hwnd, DWMWA_TEXT_COLOR, IntByReference(text), 4) }
    }
}
