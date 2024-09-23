package net.kodein.cup

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.kodein.cup.desktop.withCupSavedPresentationState
import net.kodein.cup.desktop.withCupSavedWindowState
import org.kodein.emoji.compose.EmojiUrl
import org.kodein.emoji.compose.ProvideEmojiDownloader
import org.kodein.emoji.compose.simpleDownloadBytes
import kotlin.io.path.*


private suspend fun cupDownloadEmoji(url: EmojiUrl): ByteArray = withContext(Dispatchers.IO) {
    val path = Path(".cup", "emoji", "${url.code}.${url.type.file}")
    if (path.exists()) path.readBytes()
    else {
        val bytes = simpleDownloadBytes(url)
        path.parent.createDirectories()
        path.writeBytes(bytes)
        bytes
    }
}

public actual fun cupPlatformApplication(
    title: String,
    content: @Composable () -> Unit
): Unit = application {
    println("java.vm.name = ${System.getProperty("java.vm.name")}")
    ProvideEmojiDownloader(::cupDownloadEmoji) {
        withCupSavedWindowState { visible, windowState ->
            withCupSavedPresentationState { presentationState ->
                Window(
                    title = title,
                    onCloseRequest = ::exitApplication,
                    state = windowState,
                    visible = visible,
                    onKeyEvent = PresentationKeyHandler { presentationState }.asComposeKeyHandler()
                ) {
                    if (presentationState != null) {
                        content()
                    }
                }
            }
        }
    }
}
