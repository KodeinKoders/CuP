package net.kodein.cup.widgets.material3

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp


// Linear scale down
// f(16) = 16
// f(57) = 36
private fun TextStyle.cupM3ScaleDown(): TextStyle = copy(
    fontSize = ((fontSize.value * 0.4878049f) + 8.195122f).sp,
    lineHeight = ((lineHeight.value * 0.4878049f) + 8.195122f).sp,
)

public fun Typography.cupScaleDown(): Typography = copy(
    displayLarge = displayLarge.cupM3ScaleDown(),
    displayMedium = displayMedium.cupM3ScaleDown(),
    displaySmall = displaySmall.cupM3ScaleDown(),
    headlineLarge = headlineLarge.cupM3ScaleDown(),
    headlineMedium = headlineMedium.cupM3ScaleDown(),
    headlineSmall = headlineSmall.cupM3ScaleDown(),
    titleLarge = titleLarge.cupM3ScaleDown(),
    titleMedium = titleMedium.cupM3ScaleDown(),
    titleSmall = titleSmall.cupM3ScaleDown(),
    bodyLarge = bodyLarge,
    bodyMedium = bodyMedium,
    bodySmall = bodySmall,
    labelLarge = labelLarge,
    labelMedium = labelMedium,
    labelSmall = labelSmall,
)
