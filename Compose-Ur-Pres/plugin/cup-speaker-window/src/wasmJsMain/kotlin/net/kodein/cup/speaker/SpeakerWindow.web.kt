package net.kodein.cup.speaker

import androidx.compose.runtime.Composable
import net.kodein.cup.config.CupConfigurationBuilder
import net.kodein.cup.config.CupConfigurationDsl


@CupConfigurationDsl
public actual fun CupConfigurationBuilder.speakerWindow() {}

@Composable
public actual fun isInSpeakerWindow(): Boolean = false
