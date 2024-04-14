package net.kodein.cup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color


@Composable
private fun NavigationButtons(
    weight: Modifier.() -> Modifier
) {
    val state = LocalPresentationState.current

    IconButton(
        onClick = { state.goToPrevious() },
        modifier = Modifier.weight()
    ) {
        Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back", tint = Color.White)
    }
    IconButton(
        onClick = { state.goToNext() },
        modifier = Modifier.weight()
    ) {
        Icon(Icons.AutoMirrored.Outlined.ArrowForward, "Forward", tint = Color.White)
    }

}

@Composable
internal fun PortraitMobileLayout(
    content: @Composable () -> Unit
) {
    Column {
        Box(Modifier.weight(1f)) {
            content()
        }
        Row(Modifier.background(Color.Black)) {
            NavigationButtons { weight(1f) }
        }
    }
}

@Composable
internal fun LandscapeMobileLayout(
    content: @Composable () -> Unit
) {
    Row {
        Box(Modifier.weight(1f)) {
            content()
        }
        Column(Modifier.background(Color.Black)) {
            NavigationButtons { weight(1f) }
        }
    }
}

