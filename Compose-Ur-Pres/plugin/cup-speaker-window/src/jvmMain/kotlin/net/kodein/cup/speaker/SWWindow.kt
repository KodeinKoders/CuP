package net.kodein.cup.speaker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.FilterListOff
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material.icons.rounded.ZoomOut
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import net.kodein.cup.LocalPresentationSize
import net.kodein.cup.LocalPresentationState
import net.kodein.cup.Overview
import net.kodein.cup.PresentationKeyHandler
import net.kodein.cup.PresentationState
import net.kodein.cup.SlideList
import net.kodein.cup.asComposeKeyHandler
import net.kodein.cup.currentSlide
import net.kodein.cup.goToNext
import net.kodein.cup.goToPrevious
import net.kodein.cup.laser.Laser
import net.kodein.cup.totalStepCount
import net.kodein.cup.totalStepCurrent
import net.kodein.cup.utils.CupToolsColors
import net.kodein.cup.utils.IconButtonWithTooltip

@Composable
internal fun SWWindow(
    laser: Laser?,
    setLaser: (Laser?) -> Unit,
    onCloseRequest: () -> Unit,
) {
    val presentationState = LocalPresentationState.current
    val swState = remember { SWPresentationState(presentationState) }

    remember(swState.currentPosition.slideIndex) {
        setLaser(null)
    }

    val presentationSize = LocalPresentationSize.current
    val ratio = presentationSize.width / presentationSize.height

    val updatedLaser by rememberUpdatedState(laser)

    CompositionLocalProvider(LocalPresentationState provides swState) {
        Window(
            state = rememberWindowState(width = 960.dp, height = 720.dp),
            title = "Speaker Notes",
            onCloseRequest = onCloseRequest,
            onKeyEvent = SWKeyHandler(
                laser = { updatedLaser },
                setLaser = setLaser
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                var slideListVisible by remember { mutableStateOf(false) }
                SWTopBar(
                    presentationState = swState,
                    slideListVisible = slideListVisible,
                    toggleSlideListVisible = { slideListVisible = !slideListVisible }
                )
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    SlideList(slideListVisible)
                    var boxSize: IntSize? by remember { mutableStateOf(null) }
                    Column(
                        modifier = Modifier
                            .onSizeChanged { boxSize = it }
                    ) {
                        if (!swState.isInOverview) {
                            SWMainView(
                                ratio = ratio,
                                laser = laser,
                                setLaser = setLaser,
                            )
                        } else {
                            if (boxSize != null) {
                                Overview()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SWKeyHandler(
    laser: () -> Laser?,
    setLaser: (Laser?) -> Unit
): (KeyEvent) -> Boolean {
    val state by rememberUpdatedState(LocalPresentationState.current)
    val fallback = PresentationKeyHandler { state }.asComposeKeyHandler()
    return handler@ { event ->
        if (event.type != KeyEventType.KeyDown) {
            return@handler fallback(event)
        }
        when (event.key) {
            Key.S -> true
            Key.P -> {
                when (laser()) {
                    null -> setLaser(Laser.Pointer())
                    is Laser.Pointer -> setLaser(null)
                    else -> {}
                }
                true
            }
            Key.H -> {
                when (laser()) {
                    null -> setLaser(Laser.Highlight())
                    is Laser.Highlight -> setLaser(null)
                    else -> {}
                }
                true
            }
            Key.Escape -> {
                if (laser() != null) {
                    setLaser(null)
                    true
                }
                else fallback(event)
            }
            else -> fallback(event)
        }
    }
}

@Composable
private fun SWTopBar(
    presentationState: PresentationState,
    slideListVisible: Boolean,
    toggleSlideListVisible: () -> Unit,
) {
    MaterialTheme(colorScheme = CupToolsColors.scheme) {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButtonWithTooltip(
                    text = "Slides & Steps",
                    onClick = { toggleSlideListVisible() },
                    icon = if (slideListVisible) Icons.Rounded.FilterListOff else Icons.Rounded.FilterList
                )
                Spacer(Modifier.weight(1f))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(2f)
                ) {
                    Text((presentationState.currentPosition.slideIndex + 1).toString())
                    Text(" / ")
                    Text(presentationState.slides.size.toString())
                }
                Text(
                    text = presentationState.currentSlide.name,
                    fontWeight = FontWeight.Medium,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(8f)
                )
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(4f)
                ) {
                    Text("[")
                    Text((presentationState.totalStepCurrent + 1).toString())
                    Text("..")
                    Text(presentationState.totalStepCount.toString())
                    Text("]")
                }
                Spacer(Modifier.weight(3f))
                val ltr = LocalLayoutDirection.current == LayoutDirection.Ltr
                IconButtonWithTooltip(
                    text = "Previous",
                    keys = "${if (ltr) "←" else "→"} / ↑ / ⌫",
                    onClick = { presentationState.goToPrevious() },
                    icon = if (ltr) Icons.Rounded.ChevronLeft else Icons.Rounded.ChevronRight
                )
                IconButtonWithTooltip(
                    text = "Next",
                    keys = "${if (ltr) "→" else "←"} / ↓ / ␣ / ⏎",
                    onClick = { presentationState.goToNext() },
                    icon = if (ltr) Icons.Rounded.ChevronRight else Icons.Rounded.ChevronLeft
                )
                Spacer(Modifier.weight(1f))
                IconButtonWithTooltip(
                    text = "Overview",
                    keys = "Esc",
                    onClick = { presentationState.isInOverview = !presentationState.isInOverview },
                    icon = if (presentationState.isInOverview) Icons.Rounded.ZoomIn else Icons.Rounded.ZoomOut,
                )
                Spacer(Modifier.weight(1f))
            }
        }
    }
}