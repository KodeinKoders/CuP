package net.kodein.cup

import androidx.compose.runtime.*
import kotlinx.collections.immutable.*


@Stable
public sealed interface PresentationState {

    public val currentSlideIndex: Int
    public val currentStep: Int

    public val forward: Boolean

    public var isInOverview: Boolean

    public val slides: List<Slide>

    public fun goTo(slideIndex: Int, step: Int = 0)
}

public val PresentationState.currentSlide: Slide get() {
    if (slides.isEmpty()) error("PresentationState has not been connected to a Presentation.")
    return slides[currentSlideIndex]
}

public fun PresentationState.goToNextSlide() {
    goTo(slideIndex = currentSlideIndex + 1, step = 0)
}

public fun PresentationState.goToNextStep() {
    if (currentStep == currentSlide.lastStep) {
        if (currentSlideIndex == slides.lastIndex) return
        goTo(slideIndex = currentSlideIndex + 1, step = 0)
    }
    else {
        goTo(slideIndex = currentSlideIndex, step = currentStep + 1)
    }
}

public fun PresentationState.goToNext() {
    if (isInOverview) goToNextSlide()
    else goToNextStep()
}

public fun PresentationState.goToPreviousSlide() {
    goTo(slideIndex = currentSlideIndex - 1, step = 0)
}

public fun PresentationState.goToPreviousStep() {
    if (currentStep == 0) {
        if (currentSlideIndex == 0) return
        goTo(slideIndex = currentSlideIndex - 1, step = slides[currentSlideIndex - 1].lastStep)
    } else {
        goTo(slideIndex = currentSlideIndex, step = currentStep - 1)
    }
}

public fun PresentationState.goToPrevious() {
    if (isInOverview) goToPreviousSlide()
    else goToPreviousStep()
}

public val PresentationState.totalStepCount: Int get() =
    slides.sumOf { it.stepCount }

public val PresentationState.totalStepCurrent: Int get() =
    slides.subList(0, currentSlideIndex).sumOf { it.stepCount } + currentStep

public val PresentationState.totalStepLast: Int get() =
    totalStepCount - 1

internal class PresentationStateImpl(
    private val initial: (List<Slide>) -> Pair<Int, Int> = { 0 to 0 }
) : PresentationState {

    override var slides: ImmutableList<Slide> by mutableStateOf(persistentListOf()) ; private set

    override var currentSlideIndex: Int by mutableStateOf(0) ; private set
    override var currentStep: Int by mutableStateOf(0) ; private set

    override var forward: Boolean by mutableStateOf(true) ; private set

    private var _config: PresentationConfig? by mutableStateOf(null)
    internal var config: PresentationConfig
        get() = _config ?: error("PresentationState has not been connected to a Presentation.")
        private set(value) { _config = value }

    override var isInOverview: Boolean by mutableStateOf(false)

    internal fun connect(slides: SlideGroup, config: PresentationConfig) {
        val initial: (List<Slide>) -> Pair<Int, Int> =
            if (this.slides.isEmpty()) this.initial
            else {
                val previousSlideName = this.slides.getOrNull(currentSlideIndex)?.name
                val previousStep = currentStep
                ({ newSlides ->
                    val newSlideIndex = newSlides.indexOfFirst { it.name == previousSlideName }
                    if (newSlideIndex != -1) newSlideIndex to previousStep
                    else 0 to 0
                })
            }

        val newSlides = slides.slideList
        require(newSlides.isNotEmpty()) { "Cannot connect to an empty slide List." }

        val (initialSlideIndex, initialStep) = initial(newSlides)
        val newSlideIndex = when {
            initialSlideIndex < 0 -> 0
            initialSlideIndex > newSlides.lastIndex -> newSlides.lastIndex
            else -> initialSlideIndex
        }
        val newCurrentSlide = newSlides[newSlideIndex]
        val newStep = when {
            initialStep < 0 -> 0
            initialStep > newCurrentSlide.lastStep -> newCurrentSlide.lastStep
            else -> initialStep
        }

        this.config = config
        this.slides = newSlides.toPersistentList()
        this.currentSlideIndex = newSlideIndex
        this.currentStep = newStep
    }

    private fun checkConnected() {
        check(slides.isNotEmpty()) { "PresentationState has not been connected to a Presentation." }
    }

    override fun goTo(slideIndex: Int, step: Int) {
        checkConnected()
        val newSlideIndex = when {
            slideIndex > slides.lastIndex -> slides.lastIndex
            slideIndex < 0 -> 0
            else -> slideIndex
        }
        val newSlide = slides[newSlideIndex]
        val newStep = when {
            step > newSlide.lastStep -> newSlide.lastStep
            step < 0 -> 0
            else -> step
        }

        if (newSlideIndex == currentSlideIndex && newStep == currentStep) return

        forward = when {
            newSlideIndex != currentSlideIndex -> {
                newSlideIndex > currentSlideIndex
            }
            newStep != currentStep -> {
                newStep > currentStep
            }
            else -> forward
        }

        currentSlideIndex = newSlideIndex
        currentStep = newStep
    }
}

@PluginCupAPI
public abstract class PresentationStateWrapper(public val originalState: PresentationState): PresentationState by originalState

internal fun PresentationState.impl(): PresentationStateImpl =
    when (this) {
        is PresentationStateImpl -> this
        is PresentationStateWrapper -> this.originalState.impl()
    }

public val LocalPresentationState: ProvidableCompositionLocal<PresentationState> = compositionLocalOf { error("No presentation state!") }

@Composable
@PluginCupAPI
public fun withPresentationState(
    initial: (List<Slide>) -> Pair<Int, Int> = { 0 to 0 },
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalPresentationState provides remember { PresentationStateImpl(initial) },
    ) {
        content()
    }
}
