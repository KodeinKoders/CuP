package net.kodein.cup.widgets.material

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.kodein.cup.widgets.fundation.BasicBulletPoints
import net.kodein.cup.widgets.fundation.BulletPointsBuilder

@Composable
public fun BulletPoints(
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    spacedBy: Dp = 8.dp,
    builder: BulletPointsBuilder.() -> Unit
): Unit = BasicBulletPoints(
    bulletPoint = { Text("• ") },
    modifier = modifier,
    horizontalAlignment = horizontalAlignment,
    spacedBy = spacedBy,
    builder = builder
)
