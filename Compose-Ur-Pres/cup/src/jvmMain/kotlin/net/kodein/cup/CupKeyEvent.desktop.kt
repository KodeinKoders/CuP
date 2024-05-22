package net.kodein.cup

import androidx.compose.ui.input.key.*


public actual val CupKeyEvent.key: Key get() = KeyEvent(nativeKeyEvent).key
public actual val CupKeyEvent.isCtrlPressed: Boolean get() = KeyEvent(nativeKeyEvent).isCtrlPressed
public actual val CupKeyEvent.isAltPressed: Boolean get() = KeyEvent(nativeKeyEvent).isAltPressed
public actual val CupKeyEvent.isShiftPressed: Boolean get() = KeyEvent(nativeKeyEvent).isShiftPressed
public actual val CupKeyEvent.isMetaPressed: Boolean get() = KeyEvent(nativeKeyEvent).isMetaPressed
public actual val CupKeyEvent.type: KeyEventType get() = KeyEvent(nativeKeyEvent).type

@PluginCupAPI
public fun ((CupKeyEvent) -> Boolean).asComposeKeyHandler(): (KeyEvent) -> Boolean = {
    invoke(CupKeyEvent(it.nativeKeyEvent))
}
