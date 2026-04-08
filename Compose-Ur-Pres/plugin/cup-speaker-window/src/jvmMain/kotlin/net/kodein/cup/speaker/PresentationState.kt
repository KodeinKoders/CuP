package net.kodein.cup.speaker

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import net.kodein.cup.PresentationPosition
import net.kodein.cup.PresentationState
import net.kodein.cup.PresentationStateWrapper
import net.kodein.cup.Slide
import net.kodein.cup.currentSlide

internal class ShiftedPresentationState(state: PresentationState) : PresentationStateWrapper(state) {
    override val currentPosition: PresentationPosition
        get() = PresentationPosition(
            slideIndex = when {
                originalState.currentPosition.slideIndex == originalState.slides.lastIndex -> originalState.currentPosition.slideIndex
                originalState.currentPosition.step == originalState.currentSlide.lastStep -> originalState.currentPosition.slideIndex + 1
                else -> originalState.currentPosition.slideIndex
            },
            step = when {
                originalState.currentPosition.slideIndex == originalState.slides.lastIndex && originalState.currentPosition.step == originalState.currentSlide.lastStep -> originalState.currentPosition.step
                originalState.currentPosition.step == originalState.currentSlide.lastStep -> 0
                else -> originalState.currentPosition.step + 1
            },
        )
}

internal class SWPresentationState(state: PresentationState) : PresentationStateWrapper(state) {
    override var isInOverview: Boolean by mutableStateOf(false)
}
