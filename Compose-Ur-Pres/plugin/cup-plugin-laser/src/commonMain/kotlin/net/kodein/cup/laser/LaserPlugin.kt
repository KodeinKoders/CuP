package net.kodein.cup.laser

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Draw
import androidx.compose.material.icons.rounded.Rectangle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import net.kodein.cup.CupKeyEvent
import net.kodein.cup.LocalPresentationState
import net.kodein.cup.PresentationState
import net.kodein.cup.config.CupAdditionalOverlay
import net.kodein.cup.config.CupConfigurationBuilder
import net.kodein.cup.config.CupPlugin
import net.kodein.cup.key
import net.kodein.cup.type


internal class LaserPlugin(
    val pointerKey: Pair<Key, String>?,
    val highlightKey: Pair<Key, String>?,
    val closeKey: Pair<Key, String>?,
) : CupPlugin {

    private var laser: Laser? by mutableStateOf(null)

    @Composable
    override fun BoxScope.Content() {
        val state = LocalPresentationState.current

        remember(state.currentPosition.slideIndex) {
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

    override fun overlay(state: PresentationState): List<CupAdditionalOverlay> {
        if (laser != null) {
            return listOf(
                CupAdditionalOverlay(
                    text = "Close laser",
                    keys = closeKey?.second,
                    onClick = { laser = null },
                    icon = Icons.Rounded.Close
                )
            )
        }
        return listOf(
            CupAdditionalOverlay(
                text = "Laser: Pointer & free draw",
                keys = pointerKey?.second,
                onClick = { laser = Laser.Pointer() },
                icon = Icons.Rounded.Draw
            ),
            CupAdditionalOverlay(
                text = "Laser: Highlight rectangle",
                keys = highlightKey?.second,
                onClick = { laser = Laser.Highlight() },
                icon = Icons.Rounded.Rectangle
            ),
        )
    }

    override fun onKeyEvent(event: CupKeyEvent): Boolean {
        if (event.type != KeyEventType.KeyDown) return false
        laser = when (event.key) {
            closeKey?.first if laser != null -> null
            pointerKey?.first if laser == null -> Laser.Pointer()
            pointerKey?.first if laser is Laser.Pointer -> null
            highlightKey?.first if laser == null -> Laser.Highlight()
            highlightKey?.first if laser is Laser.Highlight -> null
            else -> return false
        }
        return true
    }
}

public fun CupConfigurationBuilder.laser(
    pointerKey: Pair<Key, String>? = Key.P to "P",
    highlightKey: Pair<Key, String>? = Key.H to "H",
    closeKey: Pair<Key, String>? = Key.Escape to "Esc",
) {
    plugin(LaserPlugin(pointerKey, highlightKey, closeKey))
}
