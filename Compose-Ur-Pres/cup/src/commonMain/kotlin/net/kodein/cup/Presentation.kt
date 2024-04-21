package net.kodein.cup

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.*
import net.kodein.cup.config.CupConfigurationBuilder
import net.kodein.cup.config.CupPlugin
import net.kodein.cup.utils.Empty
import net.kodein.cup.utils.OverlayedBox
import net.kodein.cup.utils.rememberOverlayState
import kotlin.math.min


@Composable
private fun rememberRatio(
    originalDensity: Density,
    targetSize: DpSize
): Float {
    val outerContainerSize = LocalPresentationSize.current
    return remember(outerContainerSize, targetSize) {
        with(originalDensity) {
            val wRatio = outerContainerSize.width / targetSize.width.toPx()
            val hRatio = outerContainerSize.height / targetSize.height.toPx()
            min(wRatio, hRatio)
        }
    }
}

@Composable
internal fun SlideContainer(
    step: Int,
    slideSize: DpSize,
    modifier: Modifier = Modifier,
    content: SlideContent
) {
    val originalDensity = LocalDensity.current
    val ratio = rememberRatio(originalDensity, slideSize)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        CompositionLocalProvider(
            LocalDensity provides Density(originalDensity.density * ratio)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.size(slideSize)
            ) {
                content(this, step)
            }
        }
    }
}

public interface PresentationScope : BoxScope {
    @Composable
    public fun Slides()
}

private class PresentationMainViewScope(
    boxScope: BoxScope,
) : PresentationScope, BoxScope by boxScope {

    @Composable
    override fun Slides() {
        val state = LocalPresentationState.current
        val config = LocalPresentationConfig.current

        state.slides.forEachIndexed { index, slide ->
            key(slide.name) {
                val specs = config.slideSpecs(slide, index, state.lastSlideIndex)

                val visible = slide.name == state.currentSlideName

                AnimatedVisibility(
                    visible = visible,
                    enter = if (state.forward) specs.startTransitions.enterForward else specs.endTransitions.enterBackward,
                    exit = if (state.forward) specs.endTransitions.exitForward else specs.startTransitions.exitBackward,
                ) {
                    val (transitions, type) = when {
                        visible && state.forward -> specs.startTransitions to TransitionSet.Type.EnterForward
                        visible && !state.forward -> specs.endTransitions to TransitionSet.Type.EnterBackward
                        !visible && state.forward -> specs.endTransitions to TransitionSet.Type.ExitForward
                        !visible && !state.forward -> specs.startTransitions to TransitionSet.Type.ExitBackward
                        else -> error("Impossible")
                    }
                    var step by remember { mutableStateOf(0) }
                    if (visible) step = state.currentStep

                    SlideContainer(
                        step = step,
                        slideSize = specs.size,
                        modifier = transitions.modifier(this, type),
                        content = LocalSlideContents.current[index]
                    )
                }
            }
        }
    }
}

@Composable
internal fun PresentationRatioContainer(
    defaultSlideSize: DpSize,
    content: @Composable BoxScope.() -> Unit
) {
    val originalDensity by rememberUpdatedState(LocalDensity.current)

    val config = LocalPresentationConfig.current
    val outerContainerSize = LocalPresentationSize.current

    val ratio = rememberRatio(originalDensity, defaultSlideSize)

    val innerContainerSize = with (originalDensity) { (outerContainerSize / ratio).toDpSize() }

    Box(
        modifier = Modifier
            .background(config.backgroundColor)
            .fillMaxSize()
    ) {
        CompositionLocalProvider(
            LocalDensity provides Density(originalDensity.density * ratio)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(innerContainerSize)
            ) {
                content()
            }
        }
    }
}

@Composable
@PluginCupAPI
public fun PresentationMainView() {
    val config = LocalPresentationConfig.current

    PresentationRatioContainer(
        defaultSlideSize = config.defaultSpecs.size,
    ) {
        config.presentation(
            PresentationMainViewScope(
                boxScope = this,
            )
        )
    }
}

@Composable
private fun WithPresentationOverlay(
    onContainerSizeChanged: (IntSize) -> Unit,
    content: @Composable () -> Unit,
) {
    var slideListVisible by remember { mutableStateOf(false) }

    Row {
        val overlayState = rememberOverlayState()
        OverlayedBox(
            state = overlayState,
            overlay = {
                PresentationOverlay(
                    slideListVisible = slideListVisible,
                    toggleSlideListVisible = { slideListVisible = !slideListVisible },
                )
            },
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .onSizeChanged { onContainerSizeChanged(it) }
                .focusable()
                .pointerHoverIcon(if (overlayState.visible) PointerIcon.Default else PointerIcon.Empty)
        ) {
            content()
        }
        SlideList(slideListVisible)
    }
}

@PluginCupAPI
public class PresentationConfig(
    public val presentation: @Composable PresentationScope.() -> Unit,
    public val backgroundColor: Color,
    public val defaultSpecs: SlideSpecs,
    public val plugins: List<CupPlugin>,
    public val layoutDirection: LayoutDirection
) {
    public fun slideSpecs(slide: Slide, indexInGroup: Int, lastGroupIndex: Int): SlideSpecs =
        slide.specs(
            defaultSpecs,
            Slide.Configuration(
                layoutDirection = layoutDirection,
                indexInGroup = indexInGroup,
                lastGroupIndex = lastGroupIndex
            )
        )
}

@PluginCupAPI
public val LocalPresentationConfig: ProvidableCompositionLocal<PresentationConfig> = compositionLocalOf { error("No configuration") }

@PluginCupAPI
public val LocalPresentationSize: ProvidableCompositionLocal<Size> = compositionLocalOf { error("No size") }

internal val LocalSlideContents: ProvidableCompositionLocal<List<SlideContent>> = compositionLocalOf { error("no content") }

@Composable
public fun Presentation(
    slides: SlideGroup,
    configuration: CupConfigurationBuilder.() -> Unit = {},
    backgroundColor: Color = Color.LightGray,
    presentation: @Composable PresentationScope.() -> Unit = { Slides() },
) {
    val state = LocalPresentationState.current
    val scope = rememberCoroutineScope()
    remember(slides) { state.impl().connect(slides, scope) }

    var presentationSize: IntSize? by remember { mutableStateOf(null) }

    val layoutDirection = LocalLayoutDirection.current

    val applicationPresentationConfigRef = LocalApplicationPresentationConfigRef.current
    val config = remember {
        val builder = CupConfigurationBuilder().apply(configuration)
        PresentationConfig(
            presentation = presentation,
            backgroundColor = backgroundColor,
            defaultSpecs = builder.defaultSlideSpecs ?: SlideSpecs.default(layoutDirection),
            plugins = builder.plugins,
            layoutDirection = layoutDirection
        ).also {
            applicationPresentationConfigRef?.value = it
        }
    }

    val slideContents = state.slides.map { it.content() }

    CompositionLocalProvider(
        LocalPresentationConfig provides config,
        LocalSlideContents provides slideContents,
    ) {
        Box(Modifier.fillMaxSize()) {
            WithPresentationOverlay(
                onContainerSizeChanged = { presentationSize = it }
            ) {
                if (presentationSize == null) return@WithPresentationOverlay
                CompositionLocalProvider(LocalPresentationSize provides presentationSize!!.toSize()) {
                    if (state.isInOverview) {
                        Overview()
                    }
                    else {
                        PresentationMainView()
                        config.plugins.forEach {
                            with(it) { Content() }
                        }
                    }
                }
            }
        }
    }
}