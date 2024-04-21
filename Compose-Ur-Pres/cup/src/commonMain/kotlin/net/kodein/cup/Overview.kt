package net.kodein.cup

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp


private class PresentationOverviewScope(
    boxScope: BoxScope,
    val content: SlideContent,
    val step: Int,
    val slideSize: DpSize,
) : PresentationScope, BoxScope by boxScope {
    @Composable
    override fun Slides() {
        SlideContainer(
            content = content,
            step = step,
            slideSize = slideSize,
        )
    }
}

private const val shrinkRatio = 4.5f

private class OverviewPresentationState(
    state: PresentationState,
    private val slide: Slide,
    private val slideIndex: Int,
    private val step: Int
) : PresentationStateWrapper(state) {
    override val currentSlide: Slide get() = slide
    override val currentSlideName: String get() = slide.name
    override val currentSlideIndex: Int get() = slideIndex
    override val currentStep: Int get() = step
}

@Composable
private fun OverviewSlideView(
    outerContainerSize: Size,
    slide: Slide,
    index: Int,
    step: Int,
) {
    val density = LocalDensity.current

    val state = LocalPresentationState.current
    val config = LocalPresentationConfig.current

    val slideSize = config.slideSpecs(slide, index, state.lastSlideIndex).size
    val outerContainerDpSize = with(density) { outerContainerSize.toDpSize() }

    CompositionLocalProvider(LocalDensity provides Density(density.density / shrinkRatio)) {
        val alpha by animateFloatAsState(if (state.currentSlideName == slide.name && state.currentStep == step) 1f else 0f)
        Box(
            Modifier
                .border(24.dp, Color.DarkGray.copy(alpha = alpha), RoundedCornerShape(32.dp))
                .padding(56.dp)
                .pointerHoverIcon(PointerIcon.Hand)
                .clickable {
                    state.goTo(slide.name, step)
                    state.isInOverview = false
                }
        ) {
            Box(
                Modifier
                    .size(outerContainerDpSize)
                    .clipToBounds()
            ) {
                CompositionLocalProvider(
                    LocalPresentationSize provides (outerContainerSize / shrinkRatio)
                ) {
                    PresentationRatioContainer(
                        defaultSlideSize = slideSize,
                    ) {
                        CompositionLocalProvider(LocalPresentationState provides OverviewPresentationState(state, slide, index, step)) {
                            config.presentation(
                                PresentationOverviewScope(
                                    boxScope = this,
                                    content = LocalSlideContents.current[index],
                                    step = step,
                                    slideSize = slideSize,
                                )
                            )
                        }
                    }
                }
            }
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
@PluginCupAPI
public fun Overview() {
    val state = LocalPresentationState.current
    val density = LocalDensity.current
    val outerContainerSize = LocalPresentationSize.current

    val ratio = outerContainerSize.width / outerContainerSize.height

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {

        val scrollOffset = -((outerContainerSize.width - (outerContainerSize.width / shrinkRatio)) / 2).toInt()

        val hState = rememberLazyListState(state.currentSlideIndex, scrollOffset)

        LaunchedEffect(state.currentSlideIndex) {
            hState.animateScrollToItem(state.currentSlideIndex, scrollOffset)
        }

        val spacerHeightDp = with(density) {
            (outerContainerSize.height - (outerContainerSize.width / ratio / shrinkRatio)).toDp() / 2 - 12.dp
        }
        val spacerHeightPx = with(density) { spacerHeightDp.toPx() } .toInt()

        LazyRow(
            state = hState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            itemsIndexed(state.slides) { index, slide ->
                Box {
                    val vState = rememberLazyListState(if (state.currentSlideName == slide.name) state.currentStep + 2 else 2, -spacerHeightPx)
                    LaunchedEffect(state.currentSlideName, state.currentStep) {
                        if (state.currentSlideName == slide.name && slide.stepCount > 1) {
                            vState.animateScrollToItem(state.currentStep + 2, -spacerHeightPx)
                        }
                    }
                    LazyColumn(
                        state = vState,
                        userScrollEnabled = slide.stepCount > 1,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxHeight()
                    ) {
                        item { Spacer(Modifier.height(spacerHeightDp - 24.dp)/*.width(50.dp).background(Color.Red)*/) }
                        stickyHeader {
                            MaterialTheme {
                                Text(
                                    text = slide.name,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .width(with(density) { (outerContainerSize.width / shrinkRatio).toDp() } + 16.dp)
                                        .height(24.dp)
                                        .background(Color.LightGray.copy(alpha = 0.8f))
                                )
                            }
                        }
                        items(slide.stepCount) { step ->
                            OverviewSlideView(
                                outerContainerSize = Size(outerContainerSize.width, outerContainerSize.width / ratio),
                                slide = slide,
                                index = index,
                                step = step,
                            )
                        }
                        item { Spacer(Modifier.height(spacerHeightDp)) }
                    }
                }
            }
        }
        HorizontalScrollbar(
            adapter = rememberScrollbarAdapter(hState),
            modifier = Modifier.fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}
