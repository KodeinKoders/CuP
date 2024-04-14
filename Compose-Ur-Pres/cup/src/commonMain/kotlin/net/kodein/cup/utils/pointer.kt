package net.kodein.cup.utils

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import net.kodein.cup.PluginCupAPI


@Composable
internal expect fun CupTooltipArea(
    tooltip: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    delayMillis: Int = 2_000,
    content: @Composable () -> Unit
)

@Composable
@PluginCupAPI
public fun IconButtonWithTooltip(
    text: String,
    icon: ImageVector,
    keys: String? = null,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    CupTooltipArea(
        tooltip = {
            Text(text)
            if (keys != null) {
                Text(" ")
                Text("($keys)")
            }
        },
    ) {
        IconButton(
            onClick = onClick,
            enabled = enabled
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
            )
        }
    }
}

internal expect val PointerIcon.Companion.Empty: PointerIcon
