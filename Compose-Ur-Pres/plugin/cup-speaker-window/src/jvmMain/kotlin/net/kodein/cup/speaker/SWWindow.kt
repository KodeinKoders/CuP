package net.kodein.cup.speaker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.key
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
import net.kodein.cup.*
import net.kodein.cup.laser.Laser
import net.kodein.cup.utils.IconButtonWithTooltip

@Composable
internal fun SWWindow(
    laser: Laser?,
    setLaser: (Laser?) -> Unit,
    onCloseRequest: () -> Unit,
) {
    val presentationState = LocalPresentationState.current
    val swState = remember { SWPresentationState(presentationState) }

    remember(swState.currentSlideName) {
        setLaser(null)
    }

    val presentationSize = LocalPresentationSize.current
    val ratio = presentationSize.width / presentationSize.height

    val isInDrawMode by rememberUpdatedState(laser != null)

    CompositionLocalProvider(LocalPresentationState provides swState) {
        Window(
            state = rememberWindowState(width = 960.dp, height = 720.dp),
            title = "Speaker Notes",
            onCloseRequest = onCloseRequest,
            onKeyEvent = SWKeyHandler(
                isInDrawMode = { isInDrawMode },
                escapeDrawMode = { setLaser(null) }
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray)
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
    isInDrawMode: () -> Boolean,
    escapeDrawMode: () -> Unit
): (KeyEvent) -> Boolean {
    val state by rememberUpdatedState(LocalPresentationState.current)
    val fallback = PresentationKeyHandler { state }
    return { event ->
        when (event.key) {
            Key.S -> true
            Key.Escape -> {
                if (isInDrawMode()) {
                    escapeDrawMode()
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
    SWMaterialTheme {
        Surface(
            color = MaterialTheme.colors.primarySurface,
            contentColor = MaterialTheme.colors.onPrimary,
            elevation = AppBarDefaults.TopAppBarElevation,
            modifier = Modifier.fillMaxWidth()
        ) {
            val totalStepCount = presentationState.slides.sumOf { it.stepCount }
            val currentStepCount = presentationState.slides.subList(0, presentationState.currentSlideIndex)
                .sumOf { it.stepCount } + presentationState.currentStep
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
                    Text((presentationState.currentSlideIndex + 1).toString())
                    Text(" / ")
                    Text(presentationState.slides.size.toString())
                }
                Text(
                    text = presentationState.currentSlideName,
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
                    Text((currentStepCount + 1).toString())
                    Text("..")
                    Text(totalStepCount.toString())
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