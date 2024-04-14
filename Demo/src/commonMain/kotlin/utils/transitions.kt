package utils

import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.LayoutDirection
import net.kodein.cup.TransitionSet


private val y3DRSpec = tween<Float>(1_500)

@OptIn(ExperimentalAnimationApi::class)
fun y3DRotation(layoutDirection: LayoutDirection) = TransitionSet(
    enterForward = fadeIn(y3DRSpec),
    enterBackward = fadeIn(y3DRSpec),
    exitForward = fadeOut(y3DRSpec),
    exitBackward = fadeOut(y3DRSpec),
    modifier = { type ->
        val dir = when (layoutDirection) {
            LayoutDirection.Ltr -> 1
            LayoutDirection.Rtl -> -1
        }

        val rotation by transition.animateFloat(
            transitionSpec = { y3DRSpec }
        ) {
            val max = when (type) {
                TransitionSet.Type.EnterForward -> 180f * dir
                TransitionSet.Type.EnterBackward -> -180f * dir
                TransitionSet.Type.ExitForward -> -180f * dir
                TransitionSet.Type.ExitBackward -> 180f * dir
            }
            when (it) {
                EnterExitState.PreEnter -> max
                EnterExitState.PostExit -> max
                EnterExitState.Visible -> 0f
            }
        }

        Modifier
            .graphicsLayer {
                rotationY = rotation
            }
    }
)
