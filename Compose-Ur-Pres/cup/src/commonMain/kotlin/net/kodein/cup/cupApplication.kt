package net.kodein.cup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf


@PluginCupAPI
public val LocalPresentationTitle: ProvidableCompositionLocal<String> = compositionLocalOf { "Compose ur Pres" }

public fun cupApplication(
    title: String,
    content: @Composable () -> Unit
) {
    cupPlatformApplication(title) {
        CompositionLocalProvider(
            LocalPresentationTitle provides title,
        ) {
            content()
        }
    }
}

internal expect fun cupPlatformApplication(
    title: String,
    content: @Composable () -> Unit
)