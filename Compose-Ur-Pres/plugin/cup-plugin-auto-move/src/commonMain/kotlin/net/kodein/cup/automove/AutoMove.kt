package net.kodein.cup.automove

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.StopCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import kotlinx.coroutines.delay
import net.kodein.cup.CupKeyEvent
import net.kodein.cup.LocalPresentationState
import net.kodein.cup.PresentationPosition
import net.kodein.cup.PresentationState
import net.kodein.cup.config.CupAdditionalOverlay
import net.kodein.cup.config.CupConfigurationBuilder
import net.kodein.cup.config.CupPlugin
import net.kodein.cup.currentSlide
import net.kodein.cup.goToNextStep
import net.kodein.cup.key
import net.kodein.cup.lastPosition
import net.kodein.cup.type
import net.kodein.cup.utils.SlideContext
import net.kodein.cup.utils.SlideContextElement
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds


public data class AutoMovePause(
    val pause: (Int, Duration) -> Duration,
) : SlideContextElement<AutoMovePause>(Key) {
    public companion object Key : SlideContext.Key<AutoMovePause> {
        public fun onSteps(vararg steps: Int, pause: (Duration) -> Duration): AutoMovePause =
            AutoMovePause { step, default -> if (step in steps) pause(default) else default }
        public fun onSteps(vararg steps: IntRange, pause: (Duration) -> Duration): AutoMovePause =
            AutoMovePause { step, default -> if (step in steps.flatMap { it }) pause(default) else default }
    }
}

internal class AutoMovePlugin(
    private val defaultPause: Duration,
) : CupPlugin {

    var started by mutableStateOf(false)

    @Composable
    override fun BoxScope.Content() {
        val presentationState by rememberUpdatedState(LocalPresentationState.current)

        LaunchedEffect(presentationState.isInOverview) {
            if (presentationState.isInOverview) started = false
        }

        LaunchedEffect(started, presentationState.currentPosition) {
            if (started) {
                val duration = presentationState.currentSlide.context[AutoMovePause.Key]?.pause(presentationState.currentPosition.step, defaultPause) ?: defaultPause
                delay(duration)
                if (presentationState.currentPosition == presentationState.lastPosition) {
                    presentationState.goTo(PresentationPosition.START)
                } else {
                    presentationState.goToNextStep()
                }
            }
        }
    }

    override fun overlay(state: PresentationState): List<CupAdditionalOverlay> = listOf(
        CupAdditionalOverlay(
            text = "Auto Move",
            onClick = { started = !started },
            icon = if (started) Icons.Rounded.StopCircle else Icons.Rounded.PlayCircle,
            inMenu = false,
            keys = "A",
        )
    )

    override fun onKeyEvent(event: CupKeyEvent): Boolean {
        if (event.type != KeyEventType.KeyDown) return false
        if (event.key == Key.A) {
            started = !started
            return true
        }
        return false
    }
}

public fun CupConfigurationBuilder.autoMove(
    pause: Duration = 5.seconds,
) {
    plugin(AutoMovePlugin(pause))
}