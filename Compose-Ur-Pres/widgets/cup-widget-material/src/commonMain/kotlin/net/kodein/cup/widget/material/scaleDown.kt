package net.kodein.cup.widget.material

import androidx.compose.material.Typography
import net.kodein.cup.ui.cupScaleDown

public fun Typography.cupScaleDown(): Typography = copy(
    h1 = h1.cupScaleDown(),
    h2 = h2.cupScaleDown(),
    h3 = h3.cupScaleDown(),
    h4 = h4.cupScaleDown(),
    h5 = h5.cupScaleDown(),
    h6 = h6.cupScaleDown(),
)
