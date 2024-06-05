package net.kodein.cup.ui

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

// Linear scale down
// f(20) = 16
// f(96) = 32
public fun TextStyle.cupScaleDown(): TextStyle = copy(
    fontSize = ((fontSize.value * 0.2105263158) + 11.789473684).sp,
    lineHeight = ((lineHeight.value * 0.2105263158) + 11.789473684).sp,
)
