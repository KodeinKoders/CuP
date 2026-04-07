package net.kodein.cup

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.FilterListOff
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.FullscreenExit
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material.icons.rounded.ZoomOut
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import net.kodein.cup.utils.CupToolsColors
import net.kodein.cup.utils.IconButtonWithTooltip
import net.kodein.cup.utils.OverlayScope


internal val LocalFullScreenState: ProvidableCompositionLocal<Pair<Boolean, () -> Unit>?> = staticCompositionLocalOf { null }

@Composable
internal fun OverlayScope.PresentationOverlay(
    slideListVisible: Boolean,
    toggleSlideListVisible: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    fun Modifier.presentationOverlayComponent() = this
        .overlayComponent(scope)
        .padding(16.dp)
        .clip(RoundedCornerShape(8.dp))

    MaterialTheme(colorScheme = CupToolsColors.scheme) {
        val state = LocalPresentationState.current
        val config = state.config

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
                            Text("(${state.currentPosition.step})")
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
                        Text((state.currentPosition.slideIndex + 1).toString())
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
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(overlay.icon, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                                        Text(overlay.text)
                                        if (overlay.keys != null) {
                                            Text(" ")
                                            Text("(${overlay.keys})")
                                        }
                                    }
                                },
                                onClick = overlay.onClick,
                            )
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
        MaterialTheme(colorScheme = CupToolsColors.scheme) {
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
                                                visible = state.currentPosition.slideIndex == slideIndex && state.currentPosition.step == step,
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
