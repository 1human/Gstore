package zed.rainxch.githubstore.app.desktop

sealed interface KeyboardNavigationEvent {
    data object OnCtrlFClick : KeyboardNavigationEvent

    /** Escape or Alt+Left, the desktop way back out of a screen. */
    data object OnBack : KeyboardNavigationEvent
}
