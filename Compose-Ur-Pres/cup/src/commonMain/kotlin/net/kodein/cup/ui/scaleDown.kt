package net.kodein.cup.ui

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

// Linear scale down
// f(20) = 16
// f(96) = 32
public fun TextStyle.cupScaleDown(): TextStyle = copy(
    fontSize = ((fontSize.value * 0.2105263158) + 11.789473684).sp,
    lineHeight = ((lineHeight.value * 0.2105263158) + 11.789473684).sp,
)

@Deprecated(
    message = """
        `BulletPoints` has been moved to specific artefacts depending on material's version used.
            You should import either `net.kodein.cup:cup-widget-material` or `net.kodein.cup:cup-widget-material3`.""",
    level = DeprecationLevel.ERROR
)
public fun Typography.cupScaleDown(): Typography = copy(
    h1 = h1.cupScaleDown(),
    h2 = h2.cupScaleDown(),
    h3 = h3.cupScaleDown(),
    h4 = h4.cupScaleDown(),
    h5 = h5.cupScaleDown(),
    h6 = h6.cupScaleDown(),
)
