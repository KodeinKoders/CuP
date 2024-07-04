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
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import net.kodein.cup.config.CupConfiguration
import net.kodein.cup.config.CupConfigurationBuilder
import net.kodein.cup.config.CupPlugin
import net.kodein.cup.utils.Empty
import net.kodein.cup.utils.OverlayState
import net.kodein.cup.utils.OverlayedBox
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
@Composable public fun PresentationScope.SlidesContent(): Unit = Slides()

@Composable
internal fun PresentationRatioContainer(
    defaultSlideSize: DpSize,
    content: @Composable BoxScope.() -> Unit
) {
    val originalDensity by rememberUpdatedState(LocalDensity.current)

    val config = LocalPresentationState.current.impl().config
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
    val config = LocalPresentationState.current.impl().config

    PresentationRatioContainer(
        defaultSlideSize = config.defaultSpecs.size,
    ) {
        config.presentation(this) {
            val state = LocalPresentationState.current

            state.slides.forEachIndexed { slideIndex, slide ->
                key(slide.name) {
                    val specs = remember { config.slideSpecs(slide) }

                    val visible = slideIndex == state.currentSlideIndex

                    AnimatedVisibility(
                        visible = visible,
                        enter = if (state.forward) specs.startTransitions.enter(true) else specs.endTransitions.enter(false),
                        exit = if (state.forward) specs.endTransitions.exit(true) else specs.startTransitions.exit(false),
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
                            content = LocalSlideContents.current[slideIndex]
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WithPresentationOverlay(
    onContainerSizeChanged: (IntSize) -> Unit,
    content: @Composable () -> Unit,
) {
    var slideListVisible by remember { mutableStateOf(false) }

    Row {
        val overlayState = remember { OverlayState() }
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

public typealias PresentationContent = @Composable BoxScope.(@Composable () -> Unit) -> Unit

@PluginCupAPI
public class PresentationConfig(
    public val presentation: PresentationContent,
    public val backgroundColor: Color,
    public val defaultSpecs: SlideSpecs,
    public val plugins: ImmutableList<CupPlugin>,
) {
    public fun slideSpecs(slide: Slide): SlideSpecs =
        if (slide.specs != null) defaultSpecs.merge(slide.specs) else defaultSpecs
}

@PluginCupAPI
public val LocalPresentationSize: ProvidableCompositionLocal<Size> = compositionLocalOf { error("No size") }

internal val LocalSlideContents: ProvidableCompositionLocal<List<SlideContent>> = compositionLocalOf { error("no content") }

@PluginCupAPI
@Composable
public fun ProvideSlideContents(
    state: PresentationState,
    content: @Composable () -> Unit
) {
    val slideContents = state.slides.map { it.contentBuilder() }

    CompositionLocalProvider(
        value = LocalSlideContents provides slideContents,
        content = content
    )
}

@Composable
public fun Presentation(
    slides: SlideGroup,
    configuration: CupConfiguration = {},
    backgroundColor: Color = Color.LightGray,
    presentation: PresentationContent = { it() },
) {
    val state = LocalPresentationState.current
    val layoutDirection = LocalLayoutDirection.current

    val config = remember {
        val builder = CupConfigurationBuilder().apply(configuration)
        PresentationConfig(
            presentation = presentation,
            backgroundColor = backgroundColor,
            defaultSpecs = builder.defaultSlideSpecs ?: SlideSpecs.default(layoutDirection),
            plugins = builder.plugins.toImmutableList(),
        )
    }
    remember(slides) { state.impl().connect(slides, config) }

    var presentationSize: IntSize? by remember { mutableStateOf(null) }

    ProvideSlideContents(state) {
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
                    }
                    config.plugins.forEach {
                        with(it) { Content() }
                    }
                }
            }
        }
    }
}
