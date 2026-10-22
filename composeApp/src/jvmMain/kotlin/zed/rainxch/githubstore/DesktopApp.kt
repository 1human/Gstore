package zed.rainxch.githubstore

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.core.context.GlobalContext
import zed.rainxch.core.data.network.ProxyManager
import zed.rainxch.core.data.services.LocalizationManager
import zed.rainxch.core.domain.logging.KomiStoreLogger
import zed.rainxch.core.domain.model.appearance.AccentId
import zed.rainxch.core.domain.repository.ProxyRepository
import zed.rainxch.core.domain.repository.TweaksRepository
import zed.rainxch.core.domain.system.DesktopOs
import zed.rainxch.core.presentation.personality.classic.classicColorScheme
import zed.rainxch.githubstore.app.desktop.KeyboardNavigation
import zed.rainxch.githubstore.app.desktop.KeyboardNavigationEvent
import zed.rainxch.githubstore.app.di.initKoin
import zed.rainxch.githubstore.core.presentation.res.Res
import zed.rainxch.githubstore.core.presentation.res.app_icon
import zed.rainxch.githubstore.core.presentation.res.app_name
import zed.rainxch.githubstore.desktop.WindowStateStore
import zed.rainxch.githubstore.desktop.applyMacosWindowAppearance
import zed.rainxch.githubstore.desktop.applyWindowsImmersiveDarkMode
import zed.rainxch.githubstore.desktop.installMacosSystemAppearance
import zed.rainxch.githubstore.desktop.toTitleBarColorRef
import zed.rainxch.githubstore.utils.STARTUP_PREFERENCE_TIMEOUT_MS
import java.awt.Desktop
import java.awt.Dimension
import java.security.Security
import kotlin.system.exitProcess
import kotlin.time.Duration.Companion.milliseconds

// Small enough to fit on a 1080p screen, large enough for sidebar + list + detail.
private const val WINDOW_MIN_WIDTH = 1120
private const val WINDOW_MIN_HEIGHT = 720

fun main(args: Array<String>) {
    installMacosSystemAppearance()
    CrashReporter.install()

    A11yCrashGuard.install()

    selectLinuxRenderBackendIfRequested()

    val deepLinkArg = args.firstOrNull()

    if (deepLinkArg != null && DesktopDeepLink.tryForwardToRunningInstance(deepLinkArg)) {
        exitProcess(0)
    }

    Security.setProperty("networkaddress.cache.ttl", "30")
    Security.setProperty("networkaddress.cache.negative.ttl", "5")

    initKoin()

    // title/MenuBar compose outside App() and never recompose, so apply the stored language first.
    runBlocking {
        val koin = GlobalContext.get()
        val tweaksRepo = koin.get<TweaksRepository>()
        val localization = koin.get<LocalizationManager>()
        val tag =
            try {
                withTimeoutOrNull(STARTUP_PREFERENCE_TIMEOUT_MS.milliseconds) {
                    tweaksRepo.getAppLanguage().first()
                }
            } catch (_: Exception) {
                null
            }
        localization.setActiveLanguageTag(tag)
    }

    bootstrapProxy()

    DesktopDeepLink.registerUriSchemeIfNeeded()

    application {
        var deepLinkUri by mutableStateOf(deepLinkArg)
        val windowState = remember { WindowStateStore.load() }

        DisposableEffect(windowState) {
            val hook = Thread { WindowStateStore.save(windowState) }
            runCatching { Runtime.getRuntime().addShutdownHook(hook) }
            onDispose {
                runCatching { Runtime.getRuntime().removeShutdownHook(hook) }
            }
        }

        @OptIn(FlowPreview::class)
        LaunchedEffect(windowState) {
            snapshotFlow {
                Triple(windowState.placement, windowState.position, windowState.size)
            }.distinctUntilChanged()
                .debounce(500.milliseconds)
                .collect {
                    withContext(Dispatchers.IO) {
                        WindowStateStore.save(windowState)
                    }
                }
        }

        LaunchedEffect(Unit) {
            DesktopDeepLink.startInstanceListener { uri ->
                deepLinkUri = uri
            }
        }

        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().let { desktop ->
                if (desktop.isSupported(Desktop.Action.APP_OPEN_URI)) {
                    desktop.setOpenURIHandler { event ->
                        deepLinkUri = event.uri.toString()
                    }
                }
                if (desktop.isSupported(Desktop.Action.APP_ABOUT)) {
                    runCatching {
                        desktop.setAboutHandler {
                            deepLinkUri = "githubstore://tweaks/app-info"
                        }
                    }
                }
            }
        }

        Window(
            onCloseRequest = {
                WindowStateStore.save(windowState)
                exitApplication()
            },
            state = windowState,
            title = stringResource(Res.string.app_name),
            icon = painterResource(Res.drawable.app_icon),
            onKeyEvent = { keyEvent ->
                if (keyEvent.type != KeyEventType.KeyDown) {
                    false
                } else {
                    when {
                        (keyEvent.isCtrlPressed || keyEvent.isMetaPressed) && keyEvent.key == Key.F -> {
                            KeyboardNavigation.onKeyClicked(KeyboardNavigationEvent.OnCtrlFClick)
                            true
                        }

                        keyEvent.key == Key.Escape -> {
                            KeyboardNavigation.onKeyClicked(KeyboardNavigationEvent.OnBack)
                            true
                        }

                        keyEvent.isAltPressed && keyEvent.key == Key.DirectionLeft -> {
                            KeyboardNavigation.onKeyClicked(KeyboardNavigationEvent.OnBack)
                            true
                        }

                        else -> {
                            false
                        }
                    }
                }
            },
        ) {
            // Below this the sidebar + list + detail layout collapses into nonsense; keep the
            // window at a size the desktop layouts were designed for.
            LaunchedEffect(window) {
                window.minimumSize = Dimension(WINDOW_MIN_WIDTH, WINDOW_MIN_HEIGHT)
            }

            var resolvedDark by remember { mutableStateOf<Boolean?>(null) }
            LaunchedEffect(window, resolvedDark) {
                val dark = resolvedDark ?: return@LaunchedEffect
                // Paint the native caption in the app's own background colour so the
                // window frame and the page below it read as one surface.
                val scheme = classicColorScheme(accent = AccentId.WALLPAPER, dark = dark)
                applyWindowsImmersiveDarkMode(
                    window = window,
                    isDark = dark,
                    captionColorRef = scheme.background.toTitleBarColorRef(),
                    textColorRef = scheme.onBackground.toTitleBarColorRef(),
                )
                applyMacosWindowAppearance(window, dark)
            }
            App(
                deepLinkUri = deepLinkUri,
                onDeepLinkConsumed = { deepLinkUri = null },
                onResolvedDarkTheme = { resolvedDark = it },
            )
        }
    }
}

private fun bootstrapProxy() {
    val koin = GlobalContext.get()
    val appScope = koin.get<CoroutineScope>()
    val proxyRepository = koin.get<ProxyRepository>()
    val logger = koin.get<KomiStoreLogger>()

    runCatching {
        ProxyManager.bootstrap(
            repository = proxyRepository,
            appScope = appScope,
        )
    }.onFailure {
        logger.warn("$it - Proxy bootstrap failed")
    }
}

private fun selectLinuxRenderBackendIfRequested() {
    if (!DesktopOs.isLinux) return
    if (System.getProperty("skiko.renderApi") != null) return
    val fromEnv = System.getenv("SKIKO_RENDER_API")?.trim().orEmpty()
    if (fromEnv.isEmpty()) return
    System.setProperty("skiko.renderApi", fromEnv)
}
