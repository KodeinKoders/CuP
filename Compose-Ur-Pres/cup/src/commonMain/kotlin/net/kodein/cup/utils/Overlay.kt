package net.kodein.cup.utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.kodein.cup.PluginCupAPI
import kotlin.time.Duration.Companion.seconds


@PluginCupAPI
public class OverlayState(
    private val coroutineScope: CoroutineScope
) {
    public var visible: Boolean by mutableStateOf(false)

    private var hideJob: Job? = null
    public var inside: Boolean by mutableStateOf(false)
    internal set

    internal fun stopHideTimer() {
        hideJob?.cancel()
        hideJob = null
    }

    internal fun startHideTimer() {
        stopHideTimer()
        hideJob = coroutineScope.launch {
            delay(2.seconds)
            visible = false
            hideJob = null
        }
    }
}

public class OverlayScope internal constructor(
    boxScope: BoxScope,
    private val state: OverlayState
) : BoxScope by boxScope {

    @OptIn(ExperimentalComposeUiApi::class)
    public fun Modifier.overlayComponent(): Modifier = then(
        Modifier
            .onPointerEvent(PointerEventType.Enter) {
                state.inside = true
                state.stopHideTimer()
                state.visible = true
            }
            .onPointerEvent(PointerEventType.Exit) {
                state.inside = false
                state.startHideTimer()
            }
    )
}

@Composable
@PluginCupAPI
public fun rememberOverlayState(): OverlayState {
    val coroutineScope = rememberCoroutineScope()
    return remember { OverlayState(coroutineScope) }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
@PluginCupAPI
public fun OverlayedBox(
    overlay: @Composable OverlayScope.() -> Unit,
    modifier: Modifier = Modifier,
    overlayEnabled: Boolean = true,
    state: OverlayState = rememberOverlayState(),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier
            .onPointerEvent(PointerEventType.Move) {
                if (state.inside) return@onPointerEvent
                state.visible = true
                state.startHideTimer()
            }
            .onPointerEvent(PointerEventType.Press) {
                if (state.inside) return@onPointerEvent
                state.visible = true
                state.startHideTimer()
            }
    ) {
        content()
        AnimatedVisibility(
            visible = state.visible && overlayEnabled,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            val alpha by animateFloatAsState(if (state.inside) 0.6f else 0.3f)
            Box(Modifier
                .fillMaxSize()
                .alpha(alpha)
            ) {
                OverlayScope(this, state).overlay()
            }
        }
    }
}
