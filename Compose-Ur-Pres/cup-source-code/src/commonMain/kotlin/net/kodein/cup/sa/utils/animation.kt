package net.kodein.cup.sa.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout


public fun Modifier.scaleWithSize(scaleX: Float = 1f, scaleY: Float = 1f): Modifier {
    if (scaleX == 1f && scaleY == 1f) return this

    return this
        .graphicsLayer(scaleX = scaleX, scaleY = scaleY, transformOrigin = TransformOrigin(0f, 0f))
        .layout { measurable, constraints ->
            val placeable = measurable.measure(constraints)
            layout((placeable.width * scaleX).toInt(), (placeable.height * scaleY).toInt()) {
                placeable.place(0, 0)
            }
        }
}
