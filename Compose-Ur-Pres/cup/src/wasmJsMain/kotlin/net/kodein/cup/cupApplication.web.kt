package net.kodein.cup

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.window.CanvasBasedWindow
import kotlinx.browser.window
import net.kodein.cup.utils.isAnyMobile
import org.jetbrains.skiko.SkikoKeyboardEventKind
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent


@OptIn(ExperimentalComposeUiApi::class)
public actual fun cupApplication(
    title: String,
    content: @Composable () -> Unit
) {
    val isMobile = isAnyMobile()

    val (initialName, initialStep) = initialStateFromHash()

    CanvasBasedWindow(
        title = title,
        canvasElementId = "cup"
    ) {
        withPresentationState(
            initial = { slides -> slides.indexOfFirst { it.name == initialName } to initialStep }
        ) {
            SynchronizeState()

            val handler by rememberUpdatedState(PresentationKeyHandler())

            DisposableEffect(null) {
                val listener: (Event) -> Unit = { event ->
                    event as KeyboardEvent
                    val skikoEvent = toSkikoEvent(
                        event = event,
                        kind = when (event.type) {
                            "keyup" -> SkikoKeyboardEventKind.UP
                            "keydown" -> SkikoKeyboardEventKind.DOWN
                            else -> SkikoKeyboardEventKind.UNKNOWN
                        }
                    )
                    handler.invoke(KeyEvent(skikoEvent))
                }
                window.addEventListener("keyup", listener)
                window.addEventListener("keydown", listener)

                onDispose {
                    window.removeEventListener("keyup", listener)
                    window.removeEventListener("keydown", listener)
                }
            }
            if (isMobile) {
                var size: IntSize? by remember { mutableStateOf(null) }
                Box(Modifier
                    .fillMaxSize()
                    .onSizeChanged { size = it }
                ) {
                    if (size == null) return@Box
                    if (size!!.height >= size!!.width) {
                        PortraitMobileLayout(content)
                    } else {
                        LandscapeMobileLayout(content)
                    }
                }
            } else {
                content()
            }
        }
    }
}
