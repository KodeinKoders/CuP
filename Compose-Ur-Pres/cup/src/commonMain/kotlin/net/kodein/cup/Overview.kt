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
import androidx.compose.runtime.*
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
import androidx.compose.ui.unit.dp
import net.kodein.cup.utils.CupToolsColors
import net.kodein.cup.utils.CupToolsMaterialColors


private const val shrinkRatio = 4.5f

private class OverviewPresentationState(
    state: PresentationState,
    override val currentSlideIndex: Int,
    override val currentStep: Int
) : PresentationStateWrapper(state) {
    override fun goTo(slideIndex: Int, step: Int): Unit = error("Cannot move with an OverviewPresentationState")
}

@Composable
private fun OverviewSlideView(
    outerContainerSize: Size,
    slideIndex: Int,
    step: Int,
) {
    val density = LocalDensity.current

    val state = LocalPresentationState.current
    val config = state.impl().config

    val slide = state.slides[slideIndex]

    val slideSize = remember { config.slideSpecs(slide).size }
    val outerContainerDpSize = with(density) { outerContainerSize.toDpSize() }

    CompositionLocalProvider(LocalDensity provides Density(density.density / shrinkRatio)) {
        val alpha by animateFloatAsState(if (state.currentSlideIndex == slideIndex && state.currentStep == step) 1f else 0f)
        Box(
            Modifier
                .border(24.dp, CupToolsColors.dark.copy(alpha = alpha), RoundedCornerShape(32.dp))
                .padding(56.dp)
                .pointerHoverIcon(PointerIcon.Hand)
                .clickable {
                    state.goTo(slideIndex, step)
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
                        CompositionLocalProvider(LocalPresentationState provides OverviewPresentationState(state, slideIndex, step)) {
                            config.presentation(this) {
                                SlideContainer(
                                    content = LocalSlideContents.current[slideIndex],
                                    step = step,
                                    slideSize = slideSize,
                                )
                            }
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
            .background(CupToolsMaterialColors.surface)
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
            itemsIndexed(state.slides) { slideIndex, slide ->
                Box {
                    val vState = rememberLazyListState(if (state.currentSlideIndex == slideIndex) state.currentStep + 2 else 2, -spacerHeightPx)
                    LaunchedEffect(state.currentSlideIndex, state.currentStep) {
                        if (state.currentSlideIndex == slideIndex && slide.stepCount > 1) {
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
                        item { Spacer(Modifier.height(spacerHeightDp - 24.dp)) }
                        stickyHeader {
                            MaterialTheme(colors = CupToolsMaterialColors) {
                                Text(
                                    text = slide.name,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .width(with(density) { (outerContainerSize.width / shrinkRatio).toDp() } + 16.dp)
                                        .height(24.dp)
                                        .background(CupToolsMaterialColors.surface.copy(alpha = 0.8f))
                                )
                            }
                        }
                        items(slide.stepCount) { step ->
                            OverviewSlideView(
                                outerContainerSize = Size(outerContainerSize.width, outerContainerSize.width / ratio),
                                slideIndex = slideIndex,
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
