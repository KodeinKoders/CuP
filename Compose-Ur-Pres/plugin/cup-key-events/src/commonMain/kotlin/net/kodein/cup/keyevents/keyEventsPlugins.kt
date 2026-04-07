package net.kodein.cup.keyevents

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import net.kodein.cup.CupKeyEvent
import net.kodein.cup.LocalPresentationState
import net.kodein.cup.LocalSlide
import net.kodein.cup.Slide
import net.kodein.cup.config.CupConfigurationBuilder
import net.kodein.cup.config.CupPlugin
import net.kodein.cup.currentSlide


internal class KeyEventsPlugin : CupPlugin {

    val handlers = HashMap<Slide, (CupKeyEvent) -> Boolean>()
    private var currentSlide: Slide? = null

    override fun onKeyEvent(event: CupKeyEvent): Boolean {
        val handler = handlers[currentSlide] ?: return false
        return handler(event)
    }

    @Composable
    override fun BoxScope.Content() {
        currentSlide = LocalPresentationState.current.currentSlide
    }
}

@Composable
public fun OnCupKeyEvent(
    handler: (CupKeyEvent) -> Boolean
) {
    val slide = LocalSlide.current ?: error("Not in a slide")
    val state = LocalPresentationState.current
    val plugin = remember(state) {
        state.config.plugins.filterIsInstance<KeyEventsPlugin>().firstOrNull()
            ?: error("KeyEventsPlugin not found")
    }
    DisposableEffect(slide) {
        plugin.handlers[slide] = handler
        onDispose {
            plugin.handlers.remove(slide)
        }
    }
}

public fun CupConfigurationBuilder.keyEvents() {
    plugin(KeyEventsPlugin())
}
