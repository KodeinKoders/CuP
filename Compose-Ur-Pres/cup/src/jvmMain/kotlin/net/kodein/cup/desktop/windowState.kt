package net.kodein.cup.desktop

import androidx.compose.runtime.*
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import net.kodein.cup.LocalFullScreenState
import java.io.IOException
import java.util.*
import kotlin.io.path.*
import kotlin.time.Duration.Companion.seconds


private val DEFAULT_WINDOW_WIDTH = 960.dp
private val DEFAULT_WINDOW_HEIGHT = 720.dp

private data class WindowSavedState(
    val placement: WindowPlacement,
    val position: DpOffset,
    val size: DpSize
) {
    constructor(state: WindowState, previous: WindowSavedState?):
            this(
                placement = state.placement,
                position = if (previous != null && state.placement != WindowPlacement.Floating) previous.position else DpOffset(state.position.x, state.position.y),
                size = if (previous != null && state.placement != WindowPlacement.Floating) previous.size else state.size
            )

    constructor(properties: Properties):
            this(
                placement = properties.getProperty("placement")?.let { WindowPlacement.valueOf(it) } ?: WindowPlacement.Floating,
                position = DpOffset(
                    x = properties.getProperty("position.x")?.toFloatOrNull()?.dp ?: WindowPosition.PlatformDefault.x,
                    y = properties.getProperty("position.y")?.toFloatOrNull()?.dp ?: WindowPosition.PlatformDefault.y
                ),
                size = DpSize(
                    width = properties.getProperty("size.width")?.toFloatOrNull()?.dp ?: DEFAULT_WINDOW_WIDTH,
                    height = properties.getProperty("size.height")?.toFloatOrNull()?.dp ?: DEFAULT_WINDOW_HEIGHT
                )
            )

    fun applyTo(windowState: WindowState) {
        windowState.placement = placement
        windowState.position = WindowPosition(position.x, position.y)
        windowState.size = size
    }

    fun toProperties(): Properties = Properties().also {
        it.setProperty("placement", placement.name)
        it.setProperty("position.x", position.x.value.toString())
        it.setProperty("position.y", position.y.value.toString())
        it.setProperty("size.width", size.width.value.toString())
        it.setProperty("size.height", size.height.value.toString())
    }
}

@Composable
@OptIn(FlowPreview::class)
internal fun rememberCupSavedWindowState(): Pair<Boolean, WindowState> {
    val state = rememberWindowState(
        size = DpSize(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT)
    )

    var initial: WindowSavedState? by remember { mutableStateOf(null) }

    LaunchedEffect(null) {
        val props = withContext(Dispatchers.IO) {
            val windowProps = Path(".cup", "window.properties")
            if (windowProps.exists()) {
                windowProps.reader().use { reader ->
                    Properties().also { it.load(reader) }
                }
            } else null
        }
        if (props != null) {
            initial = WindowSavedState(props)
            initial!!.applyTo(state)
        } else {
            initial = WindowSavedState(state, null)
        }
    }

    if (initial != null) {
        LaunchedEffect(null) {
            try {
                var previous: WindowSavedState = initial!!
                while (true) {
                    delay(0.5.seconds)
                    val dataState = WindowSavedState(state, previous)
                    if (dataState != previous) {
                        withContext(Dispatchers.IO) {
                            Path(".cup").createDirectories()
                            Path(".cup", "window.properties").writer().use { writer ->
                                dataState.toProperties().store(writer, null)
                            }
                        }
                        previous = dataState
                    }
                }
            } catch (_: IOException) {}
        }
    }

    return (initial != null) to state
}

@Composable
public fun withCupManagedWindowState(windowState: WindowState, content: @Composable () -> Unit) {
    fun isFullScreen() = windowState.placement == WindowPlacement.Fullscreen

    CompositionLocalProvider(LocalFullScreenState provides (isFullScreen() to {
        windowState.placement = if (isFullScreen()) WindowPlacement.Floating else WindowPlacement.Fullscreen
    })) {
        content()
    }
}

@Composable
public fun withCupSavedWindowState(content: @Composable (Boolean, WindowState) -> Unit) {
    val (restored, windowState) = rememberCupSavedWindowState()
    withCupManagedWindowState(windowState) {
        content(restored, windowState)
    }
}
