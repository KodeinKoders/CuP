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
    // No tooltips in wasm yet. See https://github.com/JetBrains/compose-multiplatform/issues/4156
    content()
}

internal actual val PointerIcon.Companion.Empty: PointerIcon get() = Default
