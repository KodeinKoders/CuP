package net.kodein.cup.laser

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.toSize
import net.kodein.cup.PluginCupAPI


internal expect val PointerIcon.Companion.Empty: PointerIcon

@PluginCupAPI
public sealed class Laser {
    public abstract val drawing: Boolean

    @PluginCupAPI
    public data class Highlight(
        override val drawing: Boolean = false,
        val origin: Offset? = null,
        val pointer: Offset? = null,
    ) : Laser()

    @PluginCupAPI
    public data class Pointer(
        override val drawing: Boolean = false,
        val points: List<List<Offset>> = emptyList(),
        val pointer: Offset? = null
    ) : Laser()
}

@PluginCupAPI
@Composable
public fun LaserDisplay(laser: Laser) {
    val updatedLaser: Laser by rememberUpdatedState(laser)
    Canvas(
        Modifier
            .fillMaxSize()
    ) {
        val draw = updatedLaser
        val strokeWidth = size.minDimension / 200f
        when {
            draw is Laser.Highlight && draw.origin != null && draw.pointer != null -> {
                val o = Offset(draw.origin.x * size.width, draw.origin.y * size.height)
                val p = Offset(draw.pointer.x * size.width, draw.pointer.y * size.height)
                val rect = when {
                    p.x >= o.x && p.y >= o.y -> Rect(o, p)
                    p.x >= o.x && p.y < o.y -> Rect(Offset(o.x, p.y), Offset(p.x, o.y))
                    p.x < o.x && p.y >= o.y -> Rect(Offset(p.x, o.y), Offset(o.x, p.y))
                    p.x < o.x && p.y < o.y -> Rect(p, o)
                    else -> error("Impossible")
                }

                clipPath(
                    path = Path().apply {
                        addRect(rect)
                    },
                    clipOp = ClipOp.Difference
                ) {
                    drawRect(color = Color.Black.copy(alpha = 0.6f))
                }
                val borderTopLeft = Offset(rect.topLeft.x - strokeWidth / 2, rect.topLeft.y - strokeWidth / 2)
                val borderSize = Size(rect.size.width + strokeWidth, rect.size.height + strokeWidth)
                drawRect(color = Color.Red, topLeft = borderTopLeft, size = borderSize, style = Stroke(strokeWidth))
            }
            draw is Laser.Pointer -> {
                draw.points.forEach { points ->
                    drawPoints(
                        points = points.map { Offset(it.x * size.width, it.y * size.height) },
                        pointMode = PointMode.Polygon,
                        color = Color.Red,
                        strokeWidth = strokeWidth
                    )
                }
                if (draw.pointer != null)
                    drawPoints(
                        points = listOf(Offset(draw.pointer.x * size.width, draw.pointer.y * size.height)),
                        pointMode = PointMode.Points,
                        color = Color.Red,
                        strokeWidth = strokeWidth * 3,
                        cap = StrokeCap.Round
                    )
            }
        }
    }
}

@PluginCupAPI
@OptIn(ExperimentalComposeUiApi::class)
@Composable
public fun LaserDraw(
    laser: Laser,
    setLaser: (Laser) -> Unit,
    modifier: Modifier = Modifier,
) {
    var viewSize: Size? by remember { mutableStateOf(null) }

    fun pointerPress(event: PointerEvent) {
        val v = viewSize ?: return
        val p = event.changes.first().position
        val rp = Offset(p.x / v.width, p.y / v.height)
        if (event.button != PointerButton.Primary && !event.buttons.isPrimaryPressed) return
        when (laser) {
            is Laser.Highlight -> {
                setLaser(Laser.Highlight(origin = rp, drawing = true))
            }
            is Laser.Pointer -> {
                setLaser(
                    laser.copy(
                        points = buildList {
                            addAll(laser.points)
                            add(listOf(rp))
                        },
                        drawing = true
                    )
                )
            }
        }
    }

    fun pointerRelease() {
        when (laser) {
            is Laser.Highlight -> {
                setLaser(
                    laser.copy(drawing = false)
                )
            }
            is Laser.Pointer -> {
                setLaser(
                    laser.copy(drawing = false)
                )
            }
        }
    }

    fun pointerMove(event: PointerEvent) {
        val v = viewSize ?: return
        val change = event.changes.first()
        val p = change.position
        val rp = Offset(p.x / v.width, p.y / v.height)
        when (laser) {
            is Laser.Highlight -> {
                if (laser.drawing) {
                    setLaser(laser.copy(pointer = rp))
                }
            }
            is Laser.Pointer -> {
                val points = if (laser.drawing) buildList {
                    addAll(laser.points.subList(0, laser.points.size - 1))
                    add(laser.points.last() + rp)
                } else laser.points
                setLaser(laser.copy(
                    points = points,
                    pointer = rp
                ))
            }
        }
        if (laser.drawing && !change.pressed) {
            pointerRelease()
        }
    }

    Box(
        modifier = modifier
            .onSizeChanged { viewSize = it.toSize() }
            .then(
                when (laser) {
                    is Laser.Pointer -> Modifier.pointerHoverIcon(PointerIcon.Empty)
                    is Laser.Highlight -> Modifier.pointerHoverIcon(PointerIcon.Crosshair)
                }
            )
            .onPointerEvent(PointerEventType.Press) { pointerPress(it) }
            .onPointerEvent(PointerEventType.Release) { pointerRelease() }
            .onPointerEvent(PointerEventType.Move) { pointerMove(it) }
    ) {
        LaserDisplay(laser)
    }
}
