package net.kodein.cup.laser

import androidx.compose.ui.input.pointer.PointerIcon
import java.awt.Point
import java.awt.Toolkit
import java.awt.image.BufferedImage


private val emptyPointer = PointerIcon(
    Toolkit.getDefaultToolkit().createCustomCursor(
        BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB),
        Point(0, 0),
        "Empty Cursor"
    )
)
internal actual val PointerIcon.Companion.Empty: PointerIcon get() = emptyPointer
