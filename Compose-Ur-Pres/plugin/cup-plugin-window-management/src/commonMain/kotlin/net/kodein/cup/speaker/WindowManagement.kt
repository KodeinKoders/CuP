package net.kodein.cup.speaker

import androidx.compose.ui.input.key.Key
import net.kodein.cup.config.CupConfigurationBuilder


public expect fun CupConfigurationBuilder.windowManagement(
    fullScreenEnabled: Boolean = true,
    fullScreenKey: Pair<Key, String> = Key.F to "F",
    resizeEnabled: Boolean = true,
)
