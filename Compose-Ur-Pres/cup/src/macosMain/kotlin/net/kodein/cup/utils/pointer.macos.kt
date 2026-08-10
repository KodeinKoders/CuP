package net.kodein.cup.utils

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon


@Composable
internal actual fun CupTooltipArea(
    tooltip: @Composable RowScope.() -> Unit,
    modifier: Modifier,
    delayMillis: Int,
    content: @Composable () -> Unit
) {
    // No TooltipArea in Compose foundation for macOS yet.
    content()
}

internal actual val PointerIcon.Companion.Empty: PointerIcon get() = Default
