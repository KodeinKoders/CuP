package net.kodein.cup.speaker

import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.Key
import net.kodein.cup.config.CupConfigurationBuilder


public actual fun CupConfigurationBuilder.speakerWindow(
    key: Pair<Key, String>?,
) {}

@Composable
public actual fun isInSpeakerWindow(): Boolean = false
