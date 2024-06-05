package net.kodein.cup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import kotlin.jvm.JvmInline


@JvmInline
public value class CupKeyEvent internal constructor(internal val nativeKeyEvent: Any)

public expect val CupKeyEvent.key: Key
public expect val CupKeyEvent.isCtrlPressed: Boolean
public expect val CupKeyEvent.isAltPressed: Boolean
public expect val CupKeyEvent.isShiftPressed: Boolean
public expect val CupKeyEvent.isMetaPressed: Boolean
public expect val CupKeyEvent.type: KeyEventType


@Composable
public fun PresentationKeyHandler(
    getState: () -> PresentationState?,
): (CupKeyEvent) -> Boolean {
    val fullScreenToggle by rememberUpdatedState(LocalFullScreenState.current?.second ?: {})
    val ltr by rememberUpdatedState(LocalLayoutDirection.current == LayoutDirection.Ltr)

    return handler@ { event ->
        val state = getState() ?: return@handler false

        state.impl().config.plugins.forEach {
            if (it.onKeyEvent(event)) return@handler true
        }

        if (event.type != KeyEventType.KeyDown) return@handler false

        when (event.key) {
            Key.DirectionRight -> {
                if (event.isShiftPressed) { if (ltr) state.goToNextSlide() else state.goToPreviousSlide() }
                else { if (ltr) state.goToNext() else state.goToPrevious() }
            }
            Key.NavigateNext,
            Key.Spacebar,
            -> {
                if (event.isShiftPressed) state.goToNextSlide()
                else state.goToNext()
            }
            Key.DirectionDown -> {
                if (event.isShiftPressed) state.goToNextSlide()
                else state.goToNextStep()
            }
            Key.Enter,
            -> {
                if (state.isInOverview) state.isInOverview = false
                else {
                    if (event.isShiftPressed) state.goToNextSlide()
                    else state.goToNext()
                }
            }

            Key.DirectionLeft -> {
                if (event.isShiftPressed) { if (ltr) state.goToPreviousSlide() else state.goToNextSlide() }
                else { if (ltr) state.goToPrevious() else state.goToNext() }
            }
            Key.NavigatePrevious,
            Key.Back,
            Key.Backspace
            -> {
                if (event.isShiftPressed) state.goToPreviousSlide()
                else state.goToPrevious()
            }
            Key.DirectionUp -> {
                if (event.isShiftPressed) state.goToPreviousSlide()
                else state.goToPreviousStep()
            }
            Key.F -> {
                fullScreenToggle()
            }
            Key.Escape -> {
                state.isInOverview = !state.isInOverview
            }
        }
        true
    }
}

@Composable
public fun PresentationKeyHandler(): (CupKeyEvent) -> Boolean {
    val state = LocalPresentationState.current
    return PresentationKeyHandler { state }
}