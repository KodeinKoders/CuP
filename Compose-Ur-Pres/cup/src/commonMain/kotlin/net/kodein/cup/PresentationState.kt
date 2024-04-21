package net.kodein.cup

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.node.Ref
import kotlinx.coroutines.CoroutineScope


public sealed interface PresentationState {

    public val currentSlide: Slide
    public val currentSlideName: String
    public val currentSlideIndex: Int
    public val currentStep: Int

    public val lastSlideIndex: Int

    public val forward: Boolean

    public var isInOverview: Boolean

    public val slides: List<Slide>

    public fun goTo(slideName: String, step: Int)
    public fun goToNextSlide()
    public fun goToNextStep()
    public fun goToPreviousSlide()
    public fun goToPreviousStep()
}

public fun PresentationState.goToNext() {
    if (isInOverview) goToNextSlide()
    else goToNextStep()
}

public fun PresentationState.goToPrevious() {
    if (isInOverview) goToPreviousSlide()
    else goToPreviousStep()
}



internal class PresentationStateImpl(
    state: MutableState<Pair<String, Int>>,
) : PresentationState {
    private var pair by state
    override val currentSlideName: String get() = pair.first
    override val currentStep: Int get() = pair.second
    override var forward: Boolean = true ; private set

    private lateinit var railway: LinkedHashMap<String, Slide>

    override var isInOverview: Boolean by mutableStateOf(false)

    internal fun connect(slides: SlideGroup, scope: CoroutineScope) {
        val list = slides.slideList
        require(list.isNotEmpty()) { "Cannot connect to an empty slide List." }

        railway = list.associateByTo(LinkedHashMap()) { it.name }
        if (currentSlideName !in railway) {
            pair = railway.keys.first() to 0
            return
        }
        if (currentStep >= currentSlide.stepCount) {
            pair = currentSlideName to 0
            return
        }
    }

    private fun checkRailway() {
        check(::railway.isInitialized) { "PresentationState has not been connected to a Presentation." }
    }

    override fun goTo(slideName: String, step: Int) {
        checkRailway()
        require(slideName in railway) { "Slide '$slideName' doest not exist in connected Presentation." }
        require(step < railway[slideName]!!.stepCount) { "Slide '$slideName' has ${railway[slideName]!!.stepCount} steps, so it cannot be set to requested step $step." }

        forward = when {
            this.currentSlideName != slideName -> {
                val currentIndex = railway.keys.indexOf(this.currentSlideName)
                val goToIndex = railway.keys.indexOf(slideName)
                goToIndex > currentIndex
            }
            this.currentStep != step -> {
                step > this.currentStep
            }
            else -> forward
        }

        pair = slideName to step
    }

    override fun goToNextSlide() {
        checkRailway()
        val keyArray = railway.keys.toTypedArray()
        val index = keyArray.indexOf(currentSlideName)
        if (index == keyArray.lastIndex) return
        pair = keyArray[index + 1] to 0
        forward = true
    }

    override fun goToNextStep() {
        checkRailway()
        if (currentStep == currentSlide.lastStep) goToNextSlide()
        else {
            pair = currentSlideName to (currentStep + 1)
            forward = true
        }
    }

    override fun goToPreviousSlide() {
        checkRailway()
        val keyArray = railway.keys.toTypedArray()
        val index = keyArray.indexOf(currentSlideName)
        if (index == 0) return
        pair = keyArray[index - 1] to 0
        forward = false
    }

    override fun goToPreviousStep() {
        checkRailway()
        if (currentStep == 0) {
            val keyArray = railway.keys.toTypedArray()
            val index = keyArray.indexOf(currentSlideName)
            if (index == 0) return
            val previous = keyArray[index - 1]
            pair = previous to (railway[previous]?.lastStep ?: error("Unexpected Presentation state: current Slide '$previous' does not appear in railway."))
            forward = false
        }
        else {
            pair = currentSlideName to (currentStep - 1)
            forward = false
        }
    }

    internal companion object {
        val pairSaver = listSaver<Pair<String, Int>, Any>(
            save = { listOf(it.first, it.second) },
            restore = { (it[0] as String) to (it[1] as Int) }
        )
    }

    override val currentSlideIndex: Int
        get() {
            checkRailway()
            return railway.keys.indexOf(currentSlideName)
        }

    override val slides: List<Slide>
        get() {
            checkRailway()
            return railway.values.toList()
        }

    override val currentSlide: Slide
        get() {
            checkRailway()
            return railway[currentSlideName] ?: error("Unexpected Presentation state: current Slide '$currentSlideName' does not appear in railway.")
        }

    override val lastSlideIndex: Int
        get() {
            checkRailway()
            return railway.size - 1
        }
}

public abstract class PresentationStateWrapper(public val originalState: PresentationState): PresentationState by originalState

internal fun PresentationState.impl(): PresentationStateImpl =
    when (this) {
        is PresentationStateImpl -> this
        is PresentationStateWrapper -> this.originalState.impl()
    }

public val LocalPresentationState: ProvidableCompositionLocal<PresentationState> = compositionLocalOf { error("No presentation state!") }

internal val LocalApplicationPresentationConfigRef  = compositionLocalOf<Ref<PresentationConfig>?> { null }

@Composable
@PluginCupAPI
public fun withPresentationState(
    slide: String = "",
    step: Int = 0,
    content: @Composable () -> Unit
) {
    val pairState = rememberSaveable(stateSaver = PresentationStateImpl.pairSaver) { mutableStateOf(slide to step) }
    CompositionLocalProvider(
        LocalPresentationState provides remember { PresentationStateImpl(pairState) },
        LocalApplicationPresentationConfigRef provides remember { Ref() }
    ) {
        content()
    }
}
