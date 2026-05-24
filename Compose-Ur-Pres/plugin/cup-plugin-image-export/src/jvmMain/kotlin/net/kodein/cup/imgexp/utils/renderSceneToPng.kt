package net.kodein.cup.imgexp.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.use
import org.jetbrains.skia.EncodedImageFormat
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime


@OptIn(ExperimentalTime::class)
internal fun renderSceneToPng(
    sceneWidth: Int,
    sceneHeight: Int,
    scene: @Composable () -> Unit,
) =
    ImageComposeScene(
        width = sceneWidth,
        height = sceneHeight,
        content = scene,
    )
        .use { scene ->
            // https://youtrack.jetbrains.com/issue/CMP-6227
            scene.render()
            scene.render(1.seconds)
        }
        .use { it.encodeToData(EncodedImageFormat.PNG)!! }
        .bytes
