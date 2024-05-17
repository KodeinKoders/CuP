package net.kodein.cup.widgets.foundation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

public class BulletPointsBuilder {
    internal val contents = ArrayList<Pair<Boolean, @Composable () -> Unit>>()

    public fun BulletPoint(visible: Boolean = true, content: @Composable () -> Unit) {
        contents += visible to content
    }
}

private enum class BulletPointsSlotsEnum { Main, Dependent }



@Composable
private fun BulletPointsContent(
    bulletPoint: @Composable () -> Unit,
    contents: List<Pair<Boolean, @Composable () -> Unit>>,
    horizontalAlignment: Alignment.Horizontal,
    spacedBy: Dp,
    animationDurationMillis: Int,
    animationEasing: Easing,
) {
    Column(
        horizontalAlignment = horizontalAlignment,
    ) {
        contents.forEachIndexed { index, (visible, content) ->
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(animationDurationMillis, easing = animationEasing)) + expandVertically(tween(animationDurationMillis, easing = animationEasing)),
                exit = fadeOut(tween(animationDurationMillis, easing = animationEasing)) + shrinkVertically(tween(animationDurationMillis, easing = animationEasing)),
            ) {
                Column {
                    if (index != 0) Spacer(Modifier.height(spacedBy))
                    Row {
                        bulletPoint()
                        content()
                    }
                }
            }
        }
    }
}

@Composable
public fun BasicBulletPoints(
    bulletPoint: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    spacedBy: Dp = 8.dp,
    animationDurationMillis: Int = AnimationConstants.DefaultDurationMillis,
    animationEasing: Easing = FastOutSlowInEasing,
    builder: BulletPointsBuilder.() -> Unit
) {
    val contents = BulletPointsBuilder().apply(builder).contents

    val updatedHorizontalAlignment by rememberUpdatedState(horizontalAlignment)
    val updatedSpacedBy by rememberUpdatedState(spacedBy)
    val updatedAnimationDurationMillis by rememberUpdatedState(animationDurationMillis)
    val updatedAnimationEasing by rememberUpdatedState(animationEasing)

    SubcomposeLayout(modifier) { constraints ->
        val mainPlaceable = subcompose(BulletPointsSlotsEnum.Main) {
            BulletPointsContent(bulletPoint, contents.map { true to it.second }, updatedHorizontalAlignment, updatedSpacedBy, updatedAnimationDurationMillis, updatedAnimationEasing)
        }.first().measure(constraints)

        val sizedPlaceable = subcompose(BulletPointsSlotsEnum.Dependent) {
            BulletPointsContent(bulletPoint, contents, updatedHorizontalAlignment, updatedSpacedBy, updatedAnimationDurationMillis, updatedAnimationEasing)
        }.first().measure(constraints)

        layout(mainPlaceable.width, sizedPlaceable.height) {
            sizedPlaceable.place(0, 0)
        }
    }
}
