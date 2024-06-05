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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import net.kodein.cup.TransitionSet


private val y3DRSpec = tween<Float>(1_500)

@OptIn(ExperimentalAnimationApi::class)
val y3DRotation = TransitionSet(
    enter = { fadeIn(y3DRSpec) },
    exit = { fadeOut(y3DRSpec) },
    modifier = { type ->
        val dir = when (LocalLayoutDirection.current) {
            LayoutDirection.Ltr -> 1
            LayoutDirection.Rtl -> -1
        }

        val rotation by transition.animateFloat(
            transitionSpec = { y3DRSpec }
        ) {
            when (it) {
                EnterExitState.PreEnter -> if (type.isForward) (180f * dir) else (-180f * dir)
                EnterExitState.PostExit -> if (type.isForward) (-180f * dir) else (180f * dir)
                EnterExitState.Visible -> 0f
            }
        }

        Modifier
            .graphicsLayer {
                rotationY = rotation
            }
    }
)
