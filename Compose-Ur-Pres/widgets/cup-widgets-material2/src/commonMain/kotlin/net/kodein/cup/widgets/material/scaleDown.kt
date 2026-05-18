package net.kodein.cup.widgets.material

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp


// Linear scale down
// f(20) = 16
// f(96) = 32
private fun TextStyle.cupM2ScaleDown(): TextStyle = copy(
    fontSize = ((fontSize.value * 0.2105263158) + 11.789473684).sp,
    lineHeight = ((lineHeight.value * 0.2105263158) + 11.789473684).sp,
)

public fun Typography.cupScaleDown(): Typography = copy(
    h1 = h1.cupM2ScaleDown(),
    h2 = h2.cupM2ScaleDown(),
    h3 = h3.cupM2ScaleDown(),
    h4 = h4.cupM2ScaleDown(),
    h5 = h5.cupM2ScaleDown(),
    h6 = h6.cupM2ScaleDown(),
)
