package net.kodein.cup.utils

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import net.kodein.cup.PluginCupAPI


@PluginCupAPI
public fun cupToolsColorScheme(darkTheme: Boolean = false): ColorScheme =
    if (darkTheme) darkColorScheme() else lightColorScheme()

@PluginCupAPI
@Composable
public fun CupToolsMaterialTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = cupToolsColorScheme(darkTheme),
        typography = Typography(),
        shapes = Shapes(),
        content = content
    )
}
