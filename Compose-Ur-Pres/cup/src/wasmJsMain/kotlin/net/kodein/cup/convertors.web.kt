package net.kodein.cup

import org.jetbrains.skiko.SkikoInputModifiers
import org.jetbrains.skiko.SkikoKey
import org.jetbrains.skiko.SkikoKeyboardEvent
import org.jetbrains.skiko.SkikoKeyboardEventKind
import org.w3c.dom.events.KeyboardEvent


private fun toSkikoModifiers(event: KeyboardEvent): SkikoInputModifiers {
    var result = 0
    if (event.altKey) {
        result = result.or(SkikoInputModifiers.ALT.value)
    }
    if (event.shiftKey) {
        result = result.or(SkikoInputModifiers.SHIFT.value)
    }
    if (event.ctrlKey) {
        result = result.or(SkikoInputModifiers.CONTROL.value)
    }
    if (event.metaKey) {
        result = result.or(SkikoInputModifiers.META.value)
    }
    return SkikoInputModifiers(result)
}

private fun toSkikoKey(event: KeyboardEvent): Int {
    var key = event.keyCode
    val side = event.location
    if (side == KeyboardEvent.DOM_KEY_LOCATION_RIGHT) {
        if (
            key == SkikoKey.KEY_LEFT_CONTROL.platformKeyCode ||
            key == SkikoKey.KEY_LEFT_SHIFT.platformKeyCode ||
            key == SkikoKey.KEY_LEFT_META.platformKeyCode
        )
            key = key.or(0x80000000.toInt())
    }
    return key
}

internal fun toSkikoEvent(
    event: KeyboardEvent,
    kind: SkikoKeyboardEventKind
): SkikoKeyboardEvent {
    return SkikoKeyboardEvent(
        SkikoKey.valueOf(toSkikoKey(event)),
        toSkikoModifiers(event),
        kind,
        event.timeStamp.toDouble().toLong(),
        event
    )
}