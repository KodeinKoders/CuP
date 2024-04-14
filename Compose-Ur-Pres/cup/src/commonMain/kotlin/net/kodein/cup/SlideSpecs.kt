package net.kodein.cup

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp


public val SLIDE_SIZE_4_3: DpSize = DpSize(width = 480.dp, height = 360.dp)
public val SLIDE_SIZE_16_9: DpSize = DpSize(width = 640.dp, height = 360.dp)
public val SLIDE_SIZE_16_10: DpSize = DpSize(width = 576.dp, height = 360.dp)

public data class SlideSpecs(
    public val size: DpSize,
    public val startTransitions: TransitionSet,
    public val endTransitions: TransitionSet,
) {
    public companion object {
        public fun default(layoutDirection: LayoutDirection): SlideSpecs {
            val defaultTransitionSet = TransitionSet.moveHorizontal(layoutDirection)
            return SlideSpecs(
                size = SLIDE_SIZE_4_3,
                startTransitions = defaultTransitionSet,
                endTransitions = defaultTransitionSet
            )
        }
    }
}

public fun SlideSpecs.copyWithInsideTransitions(
    config: Slide.Configuration,
    startTransitions: TransitionSet,
    endTransitions: TransitionSet,
): SlideSpecs =
    when (config.indexInGroup) {
        0 -> copy(endTransitions = endTransitions)
        config.lastGroupIndex -> copy(startTransitions = startTransitions)
        else -> copy(startTransitions = startTransitions, endTransitions = endTransitions)
    }
