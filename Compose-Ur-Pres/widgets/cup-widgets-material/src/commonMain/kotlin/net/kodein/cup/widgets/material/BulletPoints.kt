package net.kodein.cup.widgets.material

import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import net.kodein.cup.widgets.foundation.BasicBulletPoints
import net.kodein.cup.widgets.foundation.BulletPointsBuilder

@Composable
public fun BulletPoints(
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    spacedBy: Dp = 8.dp,
    bulletSize: TextUnit = TextUnit.Unspecified,
    animationDurationMillis: Int = AnimationConstants.DefaultDurationMillis,
    animationEasing: Easing = FastOutSlowInEasing,
    builder: BulletPointsBuilder.() -> Unit
): Unit = BasicBulletPoints(
    bulletPoint = { Text("• ", fontSize = bulletSize) },
    modifier = modifier,
    horizontalAlignment = horizontalAlignment,
    spacedBy = spacedBy,
    animationDurationMillis = animationDurationMillis,
    animationEasing = animationEasing,
    builder = builder
)
