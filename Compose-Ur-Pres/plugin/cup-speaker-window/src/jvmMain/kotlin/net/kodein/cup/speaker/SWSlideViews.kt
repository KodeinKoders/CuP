package net.kodein.cup.speaker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Draw
import androidx.compose.material.icons.rounded.Rectangle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import net.kodein.cup.LocalPresentationSize
import net.kodein.cup.LocalPresentationState
import net.kodein.cup.PresentationMainView
import net.kodein.cup.PresentationState
import net.kodein.cup.laser.Laser
import net.kodein.cup.laser.LaserDraw
import net.kodein.cup.utils.*

@Composable
internal fun SWNextSlideView(
    presentationState: PresentationState,
    ratio: Float,
) {
    var viewSize: Size? by remember { mutableStateOf(null) }
    Box(
        modifier = Modifier
            .padding(8.dp)
            .aspectRatio(ratio)
            .shadow(8.dp)
            .onSizeChanged { viewSize = it.toSize() }
            .clipToBounds()
    ) {
        if (viewSize != null) {
            val shiftedPresentationState = remember(presentationState) { ShiftedPresentationState(presentationState) }
            CompositionLocalProvider(
                LocalPresentationState provides shiftedPresentationState,
                LocalPresentationSize provides viewSize!!
            ) {
                PresentationMainView()
            }
        }
    }
}

@Composable
internal fun SWCurrentSlideView(
    ratio: Float,
    laser: Laser?,
    setLaser: (Laser?) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val overlayState = remember { OverlayState() }

    var viewSize: Size? by remember { mutableStateOf(null) }

    Box(
        modifier = Modifier
            .padding(8.dp)
            .aspectRatio(ratio)
            .shadow(8.dp)
            .onSizeChanged { viewSize = it.toSize() }
            .clipToBounds()
    ) {
        OverlayedBox(
            state = overlayState,
            overlay = {
                Row(
                    Modifier
                        .overlayComponent(scope)
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CupToolsMaterialColors.surface)
                ) {
                    if (laser == null) {
                        IconButtonWithTooltip(
                            onClick = { setLaser(Laser.Pointer()) },
                            text = "Pointer & free draw (P)",
                            icon = Icons.Rounded.Draw
                        )
                        IconButtonWithTooltip(
                            onClick = { setLaser(Laser.Highlight()) },
                            text = "Highlight rectangle (H)",
                            icon = Icons.Rounded.Rectangle
                        )
                    } else {
                        IconButtonWithTooltip(
                            onClick = { setLaser(null) },
                            text = "End draw",
                            keys = "Esc",
                            icon = Icons.Rounded.Close
                        )

                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        ) {
            if (viewSize != null) {
                CompositionLocalProvider(LocalPresentationSize provides viewSize!!) {
                    PresentationMainView()
                }
            }
            if (laser != null) {
                LaserDraw(
                    laser = laser,
                    setLaser = setLaser,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}