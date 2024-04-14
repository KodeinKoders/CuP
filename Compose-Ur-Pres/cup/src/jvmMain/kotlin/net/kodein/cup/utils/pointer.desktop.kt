package net.kodein.cup.utils

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.unit.dp
import java.awt.Point
import java.awt.Toolkit
import java.awt.image.BufferedImage


@OptIn(ExperimentalFoundationApi::class)
@Composable
internal actual fun CupTooltipArea(
    tooltip: @Composable RowScope.() -> Unit,
    modifier: Modifier,
    delayMillis: Int,
    content: @Composable () -> Unit
) {
    TooltipArea(
        tooltip = {
            Surface(
                modifier = Modifier.shadow(4.dp),
                color = Color.LightGray.copy(alpha = 0.8f),
                shape = RoundedCornerShape(4.dp)
            ) {
                CompositionLocalProvider(LocalContentColor provides Color.Black) {
                    Row(Modifier.padding(4.dp)) {
                        tooltip()
                    }
                }
            }
        },
        delayMillis = delayMillis,
        content = content
    )
}

private val emptyPointer = PointerIcon(
    Toolkit.getDefaultToolkit().createCustomCursor(
        BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB),
        Point(0, 0),
        "Empty Cursor"
    )
)
internal actual val PointerIcon.Companion.Empty: PointerIcon get() = emptyPointer
