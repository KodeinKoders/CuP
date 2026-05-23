package net.kodein.cup.overview

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material.icons.rounded.ZoomOut
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import net.kodein.cup.CupKeyEvent
import net.kodein.cup.LocalPresentationState
import net.kodein.cup.Overview
import net.kodein.cup.PresentationState
import net.kodein.cup.config.CupAdditionalOverlay
import net.kodein.cup.config.CupConfigurationBuilder
import net.kodein.cup.config.CupPlugin
import net.kodein.cup.goToNextSlide
import net.kodein.cup.goToPreviousSlide
import net.kodein.cup.key
import net.kodein.cup.type
import net.kodein.cup.utils.CupToolsMaterialTheme


private class OverviewPlugin(
    val key: Pair<Key, String>?,
) : CupPlugin {
    var isInOverview by mutableStateOf(false)

    var presentationState: PresentationState? = null

    @Composable
    override fun BoxScope.Content() {
        presentationState = LocalPresentationState.current

        if (isInOverview) {
            CupToolsMaterialTheme {
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSize(.95f)
                ) {
                    Overview()
                }
            }
        }
    }

    override fun overlay(state: PresentationState): List<CupAdditionalOverlay> = listOf(
        CupAdditionalOverlay(
            text = "Overview",
            onClick = { isInOverview = !isInOverview },
            icon = if (isInOverview) Icons.Rounded.ZoomIn else Icons.Rounded.ZoomOut,
            inMenu = false,
            keys = key?.second,
        )
    )

    override fun onKeyEvent(event: CupKeyEvent): Boolean {
        if (event.type != KeyEventType.KeyDown) return false
        if (event.key == key?.first) {
            isInOverview = !isInOverview
            return true
        }
        if (isInOverview) {
            when (event.key) {
                Key.DirectionRight -> {
                    presentationState?.goToNextSlide()
                    return true
                }
                Key.DirectionLeft -> {
                    presentationState?.goToPreviousSlide()
                    return true
                }
                Key.Enter -> {
                    isInOverview = false
                    return true
                }
            }
        }
        return false
    }
}

public fun CupConfigurationBuilder.overview(
    key: Pair<Key, String>? = Key.Escape to "Esc",
) {
    plugin(OverviewPlugin(key))
}