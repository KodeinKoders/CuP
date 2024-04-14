package net.kodein.cup.speaker

import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

@Composable
internal fun SWMaterialTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = MaterialTheme.colors.copy(
            primary = Color.DarkGray,
            onPrimary = Color.White,
        )
    ) {
        CompositionLocalProvider(LocalContentColor provides Color.DarkGray) {
            content()
        }
    }
}