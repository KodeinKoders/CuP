package net.kodein.cup.laser

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Draw
import androidx.compose.material.icons.rounded.Rectangle
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import net.kodein.cup.CupKeyEvent
import net.kodein.cup.LocalPresentationState
import net.kodein.cup.config.CupAdditionalOverlay
import net.kodein.cup.config.CupConfigurationBuilder
import net.kodein.cup.config.CupConfigurationDsl
import net.kodein.cup.config.CupPlugin
import net.kodein.cup.key
import net.kodein.cup.type


internal class LaserPlugin : CupPlugin {

    private var laser: Laser? by mutableStateOf(null)

    @Composable
    override fun BoxScope.Content() {
        val state = LocalPresentationState.current

        remember(state.currentSlideIndex) {
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

    override fun overlay(): List<CupAdditionalOverlay> {
        if (laser != null) {
            return listOf(
                CupAdditionalOverlay(
                    text = "Close laser",
                    keys = "Esc",
                    onClick = { laser = null },
                    icon = Icons.Rounded.Close
                )
            )
        }
        return listOf(
            CupAdditionalOverlay(
                text = "Laser: Pointer & free draw",
                keys = "P",
                onClick = { laser = Laser.Pointer() },
                icon = Icons.Rounded.Draw
            ),
            CupAdditionalOverlay(
                text = "Laser: Highlight rectangle",
                keys = "H",
                onClick = { laser = Laser.Highlight() },
                icon = Icons.Rounded.Rectangle
            ),
        )
    }

    override fun onKeyEvent(event: CupKeyEvent): Boolean {
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
