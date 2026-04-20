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
import net.kodein.cup.PresentationState
import net.kodein.cup.config.CupAdditionalOverlay
import net.kodein.cup.config.CupConfigurationBuilder
import net.kodein.cup.config.CupPlugin
import net.kodein.cup.currentSlide
import net.kodein.cup.goToNextStep
import net.kodein.cup.key
import net.kodein.cup.type
import net.kodein.cup.utils.SlideContext
import net.kodein.cup.utils.SlideContextElement
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds


public data class AutoMovePause(
    val pause: (Int) -> Duration,
) : SlideContextElement<AutoMovePause>(Key) {
    public companion object Key : SlideContext.Key<AutoMovePause>
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
                val duration = presentationState.currentSlide.context[AutoMovePause.Key]?.pause(presentationState.currentPosition.step) ?: defaultPause
                delay(duration)
                presentationState.goToNextStep()
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