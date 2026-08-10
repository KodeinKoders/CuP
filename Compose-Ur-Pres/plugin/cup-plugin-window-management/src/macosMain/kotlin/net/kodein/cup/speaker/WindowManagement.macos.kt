package net.kodein.cup.speaker

import androidx.compose.ui.input.key.Key
import net.kodein.cup.config.CupConfigurationBuilder


public actual fun CupConfigurationBuilder.windowManagement(
    fullScreenEnabled: Boolean,
    fullScreenKey: Pair<Key, String>,
    resizeEnabled: Boolean,
) {}
