package net.kodein.cup

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.window.CanvasBasedWindow
import net.kodein.cup.utils.isAnyMobile


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
            WindowKeyHandlerEffect(handler)

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
