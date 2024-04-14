package net.kodein.cup

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp


@Composable
public fun PresentationPreview(
    slide: Slide,
    step: Int = slide.lastStep,
    previewSize: DpSize = SLIDE_SIZE_16_9,
    previewScale: Float = 1f,
    content: @Composable (SlideGroup) -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .scale(previewScale)
                .size(previewSize + DpSize(8.dp, 8.dp))
                .border(4.dp, Color.Black)
                .clipToBounds()
        ) {
            withPresentationState(slide.name, step) {
                content(slide)
            }
        }
    }
}
