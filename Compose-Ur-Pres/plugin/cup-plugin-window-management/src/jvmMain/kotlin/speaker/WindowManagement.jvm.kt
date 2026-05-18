package net.kodein.cup.speaker

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.FullscreenExit
import androidx.compose.material.icons.rounded.Transform
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.kodein.cup.CupKeyEvent
import net.kodein.cup.PresentationState
import net.kodein.cup.config.CupAdditionalOverlay
import net.kodein.cup.config.CupConfigurationBuilder
import net.kodein.cup.config.CupPlugin
import net.kodein.cup.desktop.LocalCupWindowState
import net.kodein.cup.key
import net.kodein.cup.type
import kotlin.time.Duration.Companion.milliseconds


private class WindowManagementPlugin(
    val fullScreenEnabled: Boolean,
    val fullScreenKey: Pair<Key, String>?,
    val resizeEnabled: Boolean,
) : CupPlugin {

    var windowState: WindowState? = null
    var isDialogOpen by mutableStateOf(false)

    private fun toggleFullscreen() {
        windowState?.placement = if (windowState?.placement == WindowPlacement.Fullscreen) WindowPlacement.Floating else WindowPlacement.Fullscreen
    }

    override fun overlay(state: PresentationState): List<CupAdditionalOverlay> = listOfNotNull(
        if (fullScreenEnabled) {
            CupAdditionalOverlay(
                text = "Full Screen",
                keys = fullScreenKey?.second,
                onClick = { toggleFullscreen() },
                icon = if (windowState?.placement == WindowPlacement.Fullscreen) Icons.Rounded.FullscreenExit else Icons.Rounded.Fullscreen
            )
        } else null,
        if (resizeEnabled) {
            CupAdditionalOverlay(
                text = "Resize window",
                keys = null,
                onClick = { isDialogOpen = true },
                icon = Icons.Rounded.Transform,
                inMenu = true,
            )
        } else null
    )

    override fun onKeyEvent(event: CupKeyEvent): Boolean {
        if (event.type != KeyEventType.KeyDown) return false
        if (fullScreenEnabled && event.key == fullScreenKey?.first) {
            toggleFullscreen()
            return true
        }
        return false
    }

    private suspend fun WindowState.awaitResize() {
        var previous: DpSize
        do {
            previous = size
            delay(100.milliseconds)
        } while (previous != size)
    }

    private suspend fun reduceWindowTo(ratio: Float) {
        val state = windowState ?: return

        if (state.placement != WindowPlacement.Floating) {
            val size = state.size
            state.placement = WindowPlacement.Floating
            state.awaitResize()
            state.size = DpSize(size.width - 20.dp, size.height - 20.dp)
            state.position = WindowPosition.Absolute(10.dp, 10.dp)
            state.awaitResize()
        }

        val targetWidth = state.size.height * ratio
        val targetHeight = state.size.width / ratio

        when {
            targetWidth < state.size.width -> state.size = state.size.copy(width = targetWidth)
            targetHeight < state.size.height -> state.size = state.size.copy(height = targetHeight)
        }
    }

    @Composable
    override fun BoxScope.Content() {
        windowState = LocalCupWindowState.current

        val scope = rememberCoroutineScope()
        if (isDialogOpen) {
            Dialog(
                onDismissRequest = { isDialogOpen = false },
            ) {
                Card {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Button(
                            onClick = { scope.launch { reduceWindowTo(16f / 9f) ; isDialogOpen = false } },
                            modifier = Modifier.width(256.dp)
                        ) {
                            Text("16:9")
                        }
                        Button(
                            onClick = { scope.launch { reduceWindowTo(4f / 3f) ; isDialogOpen = false } },
                            modifier = Modifier.width(256.dp)
                        ) {
                            Text("4:3")
                        }
                    }
                }
            }
        }
    }

}

public actual fun CupConfigurationBuilder.windowManagement(
    fullScreenEnabled: Boolean,
    fullScreenKey: Pair<Key, String>,
    resizeEnabled: Boolean,
) {
    plugin(WindowManagementPlugin(fullScreenEnabled, fullScreenKey, resizeEnabled))
}
