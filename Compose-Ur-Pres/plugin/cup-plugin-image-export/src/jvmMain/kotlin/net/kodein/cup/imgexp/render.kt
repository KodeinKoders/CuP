package net.kodein.cup.imgexp

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.use
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import net.kodein.cup.FixedPresentationState
import net.kodein.cup.LocalPresentationSize
import net.kodein.cup.LocalPresentationState
import net.kodein.cup.PluginCupAPI
import net.kodein.cup.PresentationMainView
import net.kodein.cup.ProvideSlideContents
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime


public suspend fun renderCupSlide(
    width: Int,
    height: Int,
    state: FixedPresentationState,
): ByteArray? =
    withContext(Dispatchers.Default) {
        repeat(10) {
            val bytes = renderCupSlideBlocking(width, height, state)
            if (bytes != null) return@withContext bytes
            delay(50.milliseconds)
        }
        null
    }

@OptIn(ExperimentalTime::class)
@PluginCupAPI
private fun renderCupSlideBlocking(
    width: Int,
    height: Int,
    state: FixedPresentationState,
): ByteArray? {
    try {
        ImageComposeScene(
            width = width,
            height = height
        ) {
            ProvideSlideContents(state) {
                CompositionLocalProvider(
                    LocalPresentationSize provides Size(width.toFloat(), height.toFloat()),
                    LocalPresentationState provides state,
                ) {
                    PresentationMainView()
                }
            }
        }.use { scene ->
            // https://youtrack.jetbrains.com/issue/CMP-6227
            scene.render()
            val img = scene.render(1.seconds)
            val bitmap = Bitmap.makeFromImage(img)
            val data = Image.makeFromBitmap(bitmap).use { image ->
                image.encodeToData(EncodedImageFormat.PNG)!!
            }
            return data.bytes
        }
    } catch (_: Throwable) {
        return null
    }
}
