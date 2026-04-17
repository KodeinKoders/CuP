package net.kodein.cup

import androidx.compose.runtime.*
import androidx.compose.ui.util.packInts
import androidx.compose.ui.util.unpackInt1
import androidx.compose.ui.util.unpackInt2
import kotlinx.collections.immutable.*
import kotlin.jvm.JvmInline


@JvmInline
public value class PresentationPosition private constructor(private val packed: Long) {
    public constructor(slideIndex: Int, step: Int): this(packInts(slideIndex, step))

    public val slideIndex: Int get() = unpackInt1(packed)
    public val step: Int get() = unpackInt2(packed)

    override fun toString(): String = "[slide $slideIndex, step $step]"

    public operator fun compareTo(other: PresentationPosition): Int =
        if (slideIndex != other.slideIndex) slideIndex.compareTo(other.slideIndex)
        else step.compareTo(other.step)
}

@Stable
public sealed interface PresentationState {

    public val currentPosition: PresentationPosition

    public val forward: Boolean

    public var isInOverview: Boolean

    public val slides: List<Slide>

    public fun goTo(position: PresentationPosition)

    @PluginCupAPI
    public val config: PresentationConfig
}

public fun PresentationState.goTo(slideIndex: Int, step: Int = 0): Unit =
    goTo(PresentationPosition(slideIndex, step))

public val PresentationState.currentSlide: Slide get() {
    if (slides.isEmpty()) error("PresentationState has not been connected to a Presentation.")
    return slides[currentPosition.slideIndex]
}

public fun PresentationState.goToNextSlide() {
    goTo(slideIndex = currentPosition.slideIndex + 1, step = 0)
}

public fun PresentationState.goToNextStep() {
    if (currentPosition.step == currentSlide.lastStep) {
        if (currentPosition.slideIndex == slides.lastIndex) return
        goTo(slideIndex = currentPosition.slideIndex + 1, step = 0)
    } else {
        goTo(slideIndex = currentPosition.slideIndex, step = currentPosition.step + 1)
    }
}

public fun PresentationState.goToNext() {
    if (isInOverview) goToNextSlide()
    else goToNextStep()
}

public fun PresentationState.goToPreviousSlide() {
    goTo(slideIndex = currentPosition.slideIndex - 1, step = 0)
}

public fun PresentationState.goToPreviousStep() {
    if (currentPosition.step == 0) {
        if (currentPosition.slideIndex == 0) return
        goTo(slideIndex = currentPosition.slideIndex - 1, step = slides[currentPosition.slideIndex - 1].lastStep)
    } else {
        goTo(slideIndex = currentPosition.slideIndex, step = currentPosition.step - 1)
    }
}

public fun PresentationState.goToPrevious() {
    if (isInOverview) goToPreviousSlide()
    else goToPreviousStep()
}

public val PresentationState.totalStepCount: Int get() =
    slides.sumOf { it.stepCount }

public val PresentationState.totalStepCurrent: Int
    get() =
        slides.subList(0, currentPosition.slideIndex).sumOf { it.stepCount } + currentPosition.step

public val PresentationState.totalStepLast: Int get() =
    totalStepCount - 1

public data class FixedPresentationState(
    override val currentPosition: PresentationPosition,
    override val forward: Boolean,
    val inOverview: Boolean,
    override val slides: List<Slide>,
    override val config: PresentationConfig
) : PresentationState {
    @Deprecated("Cannot mutate a FixedPresentationState", level = DeprecationLevel.ERROR, replaceWith = ReplaceWith(""))
    override fun goTo(position: PresentationPosition): Unit =
        error("Cannot mutate a FixedPresentationState")
    override var isInOverview: Boolean
        get() = inOverview
        @Deprecated("Cannot mutate a FixedPresentationState", level = DeprecationLevel.ERROR, replaceWith = ReplaceWith(""))
        set(value) { error("Cannot mutate a FixedPresentationState") }
}

public fun PresentationState.copyFixed(): FixedPresentationState =
    FixedPresentationState(
        currentPosition = currentPosition,
        forward = forward,
        inOverview = isInOverview,
        slides = slides,
        config = config,
    )

internal class PresentationStateImpl(
    private val initial: (List<Slide>) -> Pair<Int, Int> = { 0 to 0 }
) : PresentationState {

    override var slides: ImmutableList<Slide> by mutableStateOf(persistentListOf()) ; private set

    override var currentPosition: PresentationPosition by mutableStateOf(PresentationPosition(0, 0)) ; private set

    override var forward: Boolean by mutableStateOf(true) ; private set

    private var _config: PresentationConfig? by mutableStateOf(null)
    override var config: PresentationConfig
        get() = _config ?: error("PresentationState has not been connected to a Presentation.")
        private set(value) { _config = value }

    override var isInOverview: Boolean by mutableStateOf(false)

    internal fun connect(slides: SlideGroup, config: PresentationConfig) {
        val initial: (List<Slide>) -> Pair<Int, Int> =
            if (this.slides.isEmpty()) this.initial
            else {
                val previousSlideName = this.slides.getOrNull(currentPosition.slideIndex)?.name
                val previousStep = currentPosition.step
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
        this.currentPosition = PresentationPosition(newSlideIndex, newStep)
    }

    private fun checkConnected() {
        check(slides.isNotEmpty()) { "PresentationState has not been connected to a Presentation." }
    }

    override fun goTo(position: PresentationPosition) {
        checkConnected()
        val newSlideIndex = when {
            position.slideIndex > slides.lastIndex -> slides.lastIndex
            position.slideIndex < 0 -> 0
            else -> position.slideIndex
        }
        val newSlide = slides[newSlideIndex]
        val newPosition = PresentationPosition(
            slideIndex = newSlideIndex,
            step = when {
                position.step > newSlide.lastStep -> newSlide.lastStep
                position.step < 0 -> 0
                else -> position.step
            }
        )

        if (newPosition == currentPosition) return

        forward = newPosition > currentPosition

        currentPosition = newPosition
    }
}

@PluginCupAPI
public abstract class PresentationStateWrapper(public val originalState: PresentationState): PresentationState by originalState

internal fun PresentationState.connect(slides: SlideGroup, config: PresentationConfig): Unit =
    when (this) {
        is PresentationStateImpl -> connect(slides, config)
        is PresentationStateWrapper -> this.originalState.connect(slides, config)
        is FixedPresentationState -> error("Cannot mutate a FixedPresentationState")
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
