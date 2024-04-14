package net.kodein.cup.config

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.KeyEvent
import net.kodein.cup.PluginCupAPI


@PluginCupAPI
public data class CupAdditionalOverlay(
    val text: String,
    val onClick: () -> Unit,
    val icon: ImageVector,
    val keys: String? = null,
    val enabled: Boolean = true,
)

@PluginCupAPI
public interface CupPlugin {
    public fun onKeyEvent(event: KeyEvent): Boolean = false

    @Composable
    public fun BoxScope.Content()

    public fun overlay(): List<CupAdditionalOverlay> = emptyList()
}
