package net.kodein.cup

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified


public val SLIDE_SIZE_4_3: DpSize = DpSize(width = 480.dp, height = 360.dp)
public val SLIDE_SIZE_16_9: DpSize = DpSize(width = 640.dp, height = 360.dp)
public val SLIDE_SIZE_16_10: DpSize = DpSize(width = 576.dp, height = 360.dp)

public data class SlideSpecs(
    public val size: DpSize = DpSize.Unspecified,
    public val startTransitions: TransitionSet = TransitionSet.Unspecified,
    public val endTransitions: TransitionSet = TransitionSet.Unspecified,
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

public fun SlideSpecs.merge(other: SlideSpecs): SlideSpecs {
    val requiresAlloc = size != other.size
            || startTransitions != other.startTransitions
            || endTransitions != other.endTransitions
    if (!requiresAlloc) return this
    return SlideSpecs(
        size = if (other.size.isSpecified) other.size else this.size,
        startTransitions = if (other.startTransitions.isSpecified) other.startTransitions else this.startTransitions,
        endTransitions = if (other.endTransitions.isSpecified) other.endTransitions else this.endTransitions,
    )
}

public operator fun SlideSpecs.plus(other: SlideSpecs): SlideSpecs = merge(other)

public fun Slides.Position.insideTransitionSpecs(
    startTransitions: TransitionSet,
    endTransitions: TransitionSet
): SlideSpecs = when {
    isFirst -> SlideSpecs(endTransitions = endTransitions)
    isLast -> SlideSpecs(startTransitions = startTransitions)
    else -> SlideSpecs(startTransitions = startTransitions, endTransitions = endTransitions)
}
