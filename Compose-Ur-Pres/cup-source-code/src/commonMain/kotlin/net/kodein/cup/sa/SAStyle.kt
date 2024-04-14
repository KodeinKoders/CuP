package net.kodein.cup.sa

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.SpanStyle


public interface SAStyle {
    public fun spanStyle(): SpanStyle = SpanStyle()
    public fun DrawScope.drawBehind(rect: Rect, fraction: Float) {}
    public fun DrawScope.drawOver(rect: Rect, fraction: Float) {}

    public companion object
}

public fun SAStyle(spanStyle: SpanStyle): SAStyle = object : SAStyle {
    override fun spanStyle(): SpanStyle = spanStyle
}

public operator fun SAStyle.plus(other: SAStyle): SAStyle {
    val self = this
    return object : SAStyle {
        override fun spanStyle(): SpanStyle = self.spanStyle() + other.spanStyle()
        override fun DrawScope.drawBehind(rect: Rect, fraction: Float) {
            with(self) { drawBehind(rect, fraction) }
            with(other) { drawBehind(rect, fraction) }
        }

        override fun DrawScope.drawOver(rect: Rect, fraction: Float) {
            with(self) { drawOver(rect, fraction) }
            with(other) { drawOver(rect, fraction) }
        }
    }
}

public operator fun SAStyle.plus(spanStyle: SpanStyle): SAStyle {
    val self = this
    return object : SAStyle {
        override fun spanStyle(): SpanStyle = self.spanStyle() + spanStyle
        override fun DrawScope.drawBehind(rect: Rect, fraction: Float) = with(self) { drawBehind(rect, fraction) }
        override fun DrawScope.drawOver(rect: Rect, fraction: Float) = with(self) { drawOver(rect, fraction) }
    }
}

public fun SAStyle.Companion.line(
    color: Color,
    squiggle: Boolean = false,
    through: Boolean = false
): SAStyle = object : SAStyle {
    private fun strokeWidth(rect: Rect) = rect.height * 0.08f

    private fun DrawScope.draw(rect: Rect, fraction: Float, y: Float) {
        clipRect(rect.left, rect.top, rect.right, rect.bottom) {
            if (squiggle) {
                val step = strokeWidth(rect) * 2
                var pos = Offset(rect.left - (step / 3), y + (step / 2))
                var dir = -1
                while (pos.x < rect.right) {
                    val end = pos + Offset(step, step * dir)
                    drawLine(
                        color = color.copy(alpha = fraction),
                        start = pos,
                        end = end,
                        strokeWidth = strokeWidth(rect)
                    )
                    pos = end
                    dir = -dir
                }
            } else {
                drawLine(
                    color = color.copy(alpha = fraction),
                    start = Offset(rect.left, y),
                    end = Offset(rect.right, y),
                    strokeWidth = strokeWidth(rect)
                )
            }
        }
    }

    override fun DrawScope.drawBehind(rect: Rect, fraction: Float) {
        if (!through) {
            draw(rect, fraction, rect.bottom - strokeWidth(rect) * 1.5f)
        }
    }

    override fun DrawScope.drawOver(rect: Rect, fraction: Float) {
        if (through) {
            draw(rect, fraction, rect.height / 2)
        }
    }
}

public fun SAStyle.Companion.background(color: Color): SAStyle = object : SAStyle {
    override fun DrawScope.drawBehind(rect: Rect, fraction: Float) {
        drawRect(color.copy(alpha = color.alpha * fraction), rect.topLeft, rect.size)
    }
}