package net.kodein.cup

import androidx.compose.runtime.Composable


public expect fun cupApplication(
    title: String,
    content: @Composable () -> Unit
)
