package net.kodein.cup.laser

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import net.kodein.cup.LocalPresentationState
import net.kodein.cup.config.CupAdditionalOverlay
import net.kodein.cup.config.CupConfigurationBuilder
import net.kodein.cup.config.CupConfigurationDsl
import net.kodein.cup.config.CupPlugin


internal class LaserPlugin : CupPlugin {

    private var laser: Laser? by mutableStateOf(null)

    @Composable
    override fun BoxScope.Content() {
        val state = LocalPresentationState.current

        remember(state.currentSlideName) {
            laser = null
        }

        if (laser != null) {
            LaserDraw(
                laser = laser!!,
                setLaser = { laser = it },
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    override fun overlay(): List<CupAdditionalOverlay> = listOf(
        CupAdditionalOverlay(
            text = "Laser: Pointer & free draw",
            keys = if (laser is Laser.Pointer) "Esc" else "P",
            onClick = {
                laser = if (laser == null) Laser.Pointer() else null
            },
            icon = if (laser is Laser.Pointer) Icons.Rounded.EditOff else Icons.Rounded.Gesture,
            enabled = laser == null || laser is Laser.Pointer
        ),
        CupAdditionalOverlay(
            text = "Laser: Highlight rectangle",
            keys = if (laser is Laser.Highlight) "Esc" else "H",
            onClick = {
                laser = if (laser == null) Laser.Highlight() else null
            },
            icon = if (laser is Laser.Highlight) Icons.Rounded.EditOff else Icons.Rounded.Rectangle,
            enabled = laser == null || laser is Laser.Highlight
        ),
    )

    override fun onKeyEvent(event: KeyEvent): Boolean {
        if (event.type != KeyEventType.KeyDown) return false
        laser = when {
            event.key == Key.Escape && laser != null -> null
            event.key == Key.P && laser == null -> Laser.Pointer()
            event.key == Key.P && laser is Laser.Pointer -> null
            event.key == Key.H && laser == null -> Laser.Highlight()
            event.key == Key.H && laser is Laser.Highlight -> null
            else -> return false
        }
        return true
    }
}

@CupConfigurationDsl
public fun CupConfigurationBuilder.laser() {
    plugin(LaserPlugin())
}
