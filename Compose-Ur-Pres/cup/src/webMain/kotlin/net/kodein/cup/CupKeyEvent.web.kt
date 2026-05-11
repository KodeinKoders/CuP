package net.kodein.cup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import kotlinx.browser.window
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent

public actual val CupKeyEvent.key: Key get() = Key((nativeKeyEvent as KeyboardEvent).keyCode.toLong())
public actual val CupKeyEvent.isCtrlPressed: Boolean get() = (nativeKeyEvent as KeyboardEvent).ctrlKey
public actual val CupKeyEvent.isAltPressed: Boolean get() = (nativeKeyEvent as KeyboardEvent).altKey
public actual val CupKeyEvent.isShiftPressed: Boolean get() = (nativeKeyEvent as KeyboardEvent).shiftKey
public actual val CupKeyEvent.isMetaPressed: Boolean get() = (nativeKeyEvent as KeyboardEvent).metaKey
public actual val CupKeyEvent.type: KeyEventType get() = when ((nativeKeyEvent as KeyboardEvent).type) {
    "keyup" -> KeyEventType.KeyUp
    "keydown" -> KeyEventType.KeyDown
    else -> KeyEventType.Unknown
}


@PluginCupAPI
@Composable
public fun WindowKeyHandlerEffect(handler: (CupKeyEvent) -> Boolean) {
    DisposableEffect(null) {
        val listener: (Event) -> Unit = { event ->
            event as KeyboardEvent
            val handled = handler.invoke(CupKeyEvent(event))
            if (handled) event.stopPropagation()
        }
        window.addEventListener("keyup", listener)
        window.addEventListener("keydown", listener)

        onDispose {
            window.removeEventListener("keyup", listener)
            window.removeEventListener("keydown", listener)
        }
    }
}
