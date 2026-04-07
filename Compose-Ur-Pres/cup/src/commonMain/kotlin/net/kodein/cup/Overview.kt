package net.kodein.cup

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import net.kodein.cup.utils.CupToolsColors


private const val shrinkRatio = 4.5f

private class OverviewPresentationState(
    state: PresentationState,
    override val currentPosition: PresentationPosition,
) : PresentationStateWrapper(state) {
    override fun goTo(position: PresentationPosition): Unit = error("Cannot move with an OverviewPresentationState")
}

@Composable
private fun OverviewSlideView(
    outerContainerSize: Size,
    position: PresentationPosition,
) {
    val density = LocalDensity.current

    val state = LocalPresentationState.current
    val config = state.config

    val slide = state.slides[position.slideIndex]

    val slideSize = remember { config.slideSpecs(slide).size }
    val outerContainerDpSize = with(density) { outerContainerSize.toDpSize() }

    CompositionLocalProvider(LocalDensity provides Density(density.density / shrinkRatio)) {
        val alpha by animateFloatAsState(if (state.currentPosition == position) 1f else 0f)
        Box(
            Modifier
                .border(24.dp, CupToolsColors.dark.copy(alpha = alpha), RoundedCornerShape(32.dp))
                .padding(56.dp)
                .pointerHoverIcon(PointerIcon.Hand)
                .clickable {
                    state.goTo(position)
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
                        CompositionLocalProvider(LocalPresentationState provides OverviewPresentationState(state, position)) {
                            config.presentation(this) {
                                SlideContainer(
                                    slide = slide,
                                    step = position.step,
                                    slideSize = slideSize,
                                    content = LocalSlideContents.current[position.slideIndex],
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
            .background(MaterialTheme.colorScheme.surface)
    ) {

        val scrollOffset = -((outerContainerSize.width - (outerContainerSize.width / shrinkRatio)) / 2).toInt()

        val hState = rememberLazyListState(state.currentPosition.slideIndex, scrollOffset)

        LaunchedEffect(state.currentPosition.slideIndex) {
            hState.animateScrollToItem(state.currentPosition.slideIndex, scrollOffset)
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
                    val vState = rememberLazyListState(if (state.currentPosition.slideIndex == slideIndex) state.currentPosition.step + 2 else 2, -spacerHeightPx)
                    LaunchedEffect(state.currentPosition.slideIndex, state.currentPosition.step) {
                        if (state.currentPosition.slideIndex == slideIndex && slide.stepCount > 1) {
                            vState.animateScrollToItem(state.currentPosition.step + 2, -spacerHeightPx)
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
                            MaterialTheme(colorScheme = CupToolsColors.scheme) {
                                Text(
                                    text = slide.name,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .width(with(density) { (outerContainerSize.width / shrinkRatio).toDp() } + 16.dp)
                                        .height(24.dp)
                                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                                )
                            }
                        }
                        items(slide.stepCount) { step ->
                            OverviewSlideView(
                                outerContainerSize = Size(outerContainerSize.width, outerContainerSize.width / ratio),
                                position = PresentationPosition(slideIndex, step),
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
