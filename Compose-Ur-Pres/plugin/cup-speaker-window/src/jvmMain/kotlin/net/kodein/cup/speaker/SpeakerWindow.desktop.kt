package net.kodein.cup.speaker

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.SpeakerNotes
import androidx.compose.material.icons.rounded.SpeakerNotesOff
import androidx.compose.runtime.*
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import net.kodein.cup.CupKeyEvent
import net.kodein.cup.PresentationState
import net.kodein.cup.config.CupAdditionalOverlay
import net.kodein.cup.config.CupConfigurationBuilder
import net.kodein.cup.config.CupConfigurationDsl
import net.kodein.cup.config.CupPlugin
import net.kodein.cup.key
import net.kodein.cup.laser.Laser
import net.kodein.cup.laser.LaserDisplay
import net.kodein.cup.type


internal class SpeakerNotesPlugin : CupPlugin {

    var isOpen by mutableStateOf(false)

    @Composable
    override fun BoxScope.Content() {
        var laser: Laser? by remember { mutableStateOf(null) }
        if (isOpen) {
            SWWindow(
                laser = laser,
                setLaser = { laser = it },
                onCloseRequest = {
                    isOpen = false
                    laser = null
                },
            )
        }
        if (laser != null) {
            LaserDisplay(laser!!)
        }
    }

    override fun overlay(state: PresentationState): List<CupAdditionalOverlay> = listOf(
        CupAdditionalOverlay(
            text = "Speaker notes",
            keys = "S",
            onClick = { isOpen = !isOpen },
            icon = if (isOpen) Icons.Rounded.SpeakerNotesOff else Icons.AutoMirrored.Rounded.SpeakerNotes
        )
    )

    override fun onKeyEvent(event: CupKeyEvent): Boolean {
        if (event.type != KeyEventType.KeyDown) return false
        if (event.key == Key.S) {
            isOpen = !isOpen
            return true
        }
        return false
    }
}

@CupConfigurationDsl
public actual fun CupConfigurationBuilder.speakerWindow() {
    plugin(SpeakerNotesPlugin())
}
