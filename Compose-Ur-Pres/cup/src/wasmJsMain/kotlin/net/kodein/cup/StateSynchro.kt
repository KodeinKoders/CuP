package net.kodein.cup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.browser.window


internal fun initialStateFromHash(): Pair<String, Int> {
    val hash = window.location.hash.removePrefix("#")
    return if (hash.isNotEmpty()) {
        val split = hash.split("/")
        (split.getOrNull(0) ?: "") to (split.getOrNull(1)?.toIntOrNull() ?: 0)
    } else ("" to 0)
}

@Composable
internal fun SynchronizeState() {

    val state = LocalPresentationState.current

    remember(state.slides, state.currentSlideIndex, state.currentStep) {
        if (state.slides.isEmpty()) return@remember
        var hash = state.currentSlide.name
        if (state.currentStep != 0) hash += "/${state.currentStep}"
        window.location.hash = hash
    }

}