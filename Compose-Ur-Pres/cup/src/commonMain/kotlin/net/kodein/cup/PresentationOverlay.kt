package net.kodein.cup

import androidx.compose.animation.*
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import net.kodein.cup.utils.CupToolsMaterialColors
import net.kodein.cup.utils.IconButtonWithTooltip
import net.kodein.cup.utils.OverlayScope


internal val LocalFullScreenState: ProvidableCompositionLocal<Pair<Boolean, () -> Unit>?> = staticCompositionLocalOf { null }

@Composable
internal fun OverlayScope.PresentationOverlay(
    slideListVisible: Boolean,
    toggleSlideListVisible: () -> Unit,
) {
    fun Modifier.presentationOverlayComponent() = this
        .overlayComponent()
        .padding(16.dp)
        .clip(RoundedCornerShape(8.dp))

    MaterialTheme(colors = CupToolsMaterialColors) {
        val state = LocalPresentationState.current
        val config = state.impl().config

        if (!state.isInOverview) {
            Surface(
                Modifier
                    .align(Alignment.TopCenter)
                    .presentationOverlayComponent()
            ) {
                SelectionContainer {
                    Row(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
                        Text(state.currentSlide.name)
                        if (state.currentSlide.stepCount > 1) {
                            Text(" ")
                            Text("(${state.currentStep})")
                        }
                    }
                }
            }
        }

        if (!state.isInOverview) {
            Surface(
                Modifier
                    .align(Alignment.BottomStart)
                    .presentationOverlayComponent()
            ) {
                SelectionContainer {
                    Row(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
                        Text((state.currentSlideIndex + 1).toString())
                        Text(" / ")
                        Text(state.slides.size.toString())
                    }
                }
            }
        }

        if (state.isInOverview) {
            Surface(
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 16.dp)
                    .presentationOverlayComponent()
            ) {
                Text(
                    text = "Use ⇧ to scroll horizontally",
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                )
            }
        }

        Surface(
            Modifier
                .align(Alignment.TopStart)
                .presentationOverlayComponent()
        ) {
            Column {
                val fullScreenState = LocalFullScreenState.current
                if (fullScreenState != null) {
                    val (isFullScreen, toggleFullScreen) = fullScreenState
                    IconButtonWithTooltip(
                        text = "Full Screen",
                        keys = "F",
                        onClick = { toggleFullScreen() },
                        icon = if (isFullScreen) Icons.Rounded.FullscreenExit else Icons.Rounded.Fullscreen
                    )
                }
                IconButtonWithTooltip(
                    text = "Overview",
                    keys = "Esc",
                    onClick = { state.isInOverview = !state.isInOverview },
                    icon = if (state.isInOverview) Icons.Rounded.ZoomIn else Icons.Rounded.ZoomOut
                )
                val overlays = config.plugins.flatMap { it.overlay(state) }
                val mainOverlays = overlays.filter { !it.inMenu }
                val menuOverlays = overlays.filter { it.inMenu }
                mainOverlays.forEach { overlay ->
                    IconButtonWithTooltip(
                        text = overlay.text,
                        keys = overlay.keys,
                        onClick = overlay.onClick,
                        icon = overlay.icon,
                        enabled = overlay.enabled
                    )
                }
                if (menuOverlays.isNotEmpty()) {
                    var showMenu by remember { mutableStateOf(false) }
                    IconButton(
                        onClick = { showMenu = !showMenu }
                    ) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        menuOverlays.forEach { overlay ->
                            DropdownMenuItem(onClick = overlay.onClick) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(overlay.icon, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                                    Text(overlay.text)
                                    if (overlay.keys != null) {
                                        Text(" ")
                                        Text("(${overlay.keys})")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Surface(
            Modifier
                .align(Alignment.TopEnd)
                .presentationOverlayComponent()
        ) {
            IconButtonWithTooltip(
                text = "Slides & Steps",
                onClick = { toggleSlideListVisible() },
                icon = if (slideListVisible) Icons.Rounded.FilterListOff else Icons.Rounded.FilterList
            )
        }

        Surface(
            Modifier
                .align(Alignment.BottomEnd)
                .presentationOverlayComponent()
        ) {
            Row {
                val ltr = LocalLayoutDirection.current == LayoutDirection.Ltr
                IconButtonWithTooltip(
                    text = "Previous",
                    keys = "${if (ltr) "←" else "→"} / ↑ / ⌫",
                    onClick = { state.goToPrevious() },
                    icon = if (ltr) Icons.Rounded.ChevronLeft else Icons.Rounded.ChevronRight
                )
                IconButtonWithTooltip(
                    text = "Next",
                    keys = "${if (ltr) "→" else "←"} / ↓ / ␣ / ⏎",
                    onClick = { state.goToNext() },
                    icon = if (ltr) Icons.Rounded.ChevronRight else Icons.Rounded.ChevronLeft
                )
            }
        }
    }
}

@Composable
@PluginCupAPI
public fun SlideList(
    visible: Boolean,
) {
    AnimatedVisibility(
        visible = visible,
        enter = expandIn(expandFrom = Alignment.Center) { IntSize(0, it.height) },
        exit = shrinkOut(shrinkTowards = Alignment.Center) { IntSize(0, it.height) }
    ) {
        MaterialTheme(colors = CupToolsMaterialColors) {
            Surface(
                Modifier
                    .fillMaxHeight()
                    .width(300.dp)
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(vertical = 4.dp)
                ) {
                    val state = LocalPresentationState.current
                    val lazyListState = rememberLazyListState()
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                        state = lazyListState
                    ) {
                        state.slides.forEachIndexed { slideIndex, slide ->
                            item { Spacer(Modifier.height(12.dp)) }
                            repeat(slide.stepCount) { step ->
                                item {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { state.goTo(slideIndex, step) }
                                            .padding(vertical = 6.dp)
                                            .padding(end = 4.dp)
                                    ) {
                                        Box(Modifier.size(32.dp)) {
                                            androidx.compose.animation.AnimatedVisibility(
                                                visible = state.currentSlideIndex == slideIndex && state.currentStep == step,
                                                enter = fadeIn() + slideIn { IntOffset(-it.width / 2, 0) },
                                                exit = fadeOut() + slideOut { IntOffset(-it.width / 2, 0) },
                                            ) {
                                                Icon(if (LocalLayoutDirection.current == LayoutDirection.Ltr) Icons.Rounded.ChevronRight else Icons.Rounded.ChevronLeft, "Current", Modifier.fillMaxSize())
                                            }
                                        }
                                        Text(
                                            text = slide.name,
                                            modifier = Modifier
                                                .padding(start = 4.dp)
                                                .then(
                                                    if (step != 0) Modifier.alpha(0.2f)
                                                    else Modifier
                                                )
                                        )
                                        if (slide.stepCount > 1) {
                                            Text(
                                                text = step.toString(),
                                                modifier = Modifier
                                                    .padding(start = 8.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    VerticalScrollbar(
                        adapter = rememberScrollbarAdapter(lazyListState),
                        modifier = Modifier
                            .fillMaxHeight()
                            .align(Alignment.CenterEnd)
                    )
                }
            }
        }
    }
}
