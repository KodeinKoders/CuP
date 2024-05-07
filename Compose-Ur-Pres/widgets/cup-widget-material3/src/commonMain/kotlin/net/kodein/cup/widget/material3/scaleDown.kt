package net.kodein.cup.widget.material3

import androidx.compose.material3.Typography
import net.kodein.cup.ui.cupScaleDown

public fun Typography.cupScaleDown(): Typography = copy(
    displayLarge = displayLarge.cupScaleDown(),
    displayMedium = displayMedium.cupScaleDown(),
    displaySmall = displaySmall.cupScaleDown(),
    headlineLarge = headlineLarge.cupScaleDown(),
    headlineMedium = headlineMedium.cupScaleDown(),
    headlineSmall = headlineSmall.cupScaleDown(),
    titleLarge = titleLarge.cupScaleDown(),
    titleMedium = titleMedium.cupScaleDown(),
    titleSmall = titleSmall.cupScaleDown(),
    bodyLarge = bodyLarge.cupScaleDown(),
    bodyMedium = bodyMedium.cupScaleDown(),
    bodySmall = bodySmall.cupScaleDown(),
    labelLarge = labelLarge.cupScaleDown(),
    labelMedium = labelMedium.cupScaleDown(),
    labelSmall = labelSmall.cupScaleDown(),
)