package net.kodein.cup.speaker

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import net.kodein.cup.PresentationState
import net.kodein.cup.PresentationStateWrapper
import net.kodein.cup.Slide

internal class ShiftedPresentationState(state: PresentationState) : PresentationStateWrapper(state) {
    override val currentSlide: Slide get() = originalState.slides[currentSlideIndex]
    override val currentSlideName: String get() = currentSlide.name
    override val currentSlideIndex: Int get() = when {
        originalState.currentSlideIndex == originalState.slides.lastIndex -> originalState.currentSlideIndex
        originalState.currentStep == originalState.currentSlide.lastStep -> originalState.currentSlideIndex + 1
        else -> originalState.currentSlideIndex
    }
    override val currentStep: Int get() = when {
        originalState.currentSlideIndex == originalState.slides.lastIndex && originalState.currentStep == originalState.currentSlide.lastStep -> originalState.currentStep
        originalState.currentStep == originalState.currentSlide.lastStep -> 0
        else -> originalState.currentStep + 1
    }
}

internal class SWPresentationState(state: PresentationState) : PresentationStateWrapper(state) {
    override var isInOverview: Boolean by mutableStateOf(false)
}
