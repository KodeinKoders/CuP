package net.kodein.cup

import androidx.compose.animation.*
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection


/**
 * Holds the transitions to be applied to a Slide when entering or exiting the screen.
 *
 * @param enterForward: The transition applied to the slide when it enters the screen from the previous slide
 *                      (user navigated forward).
 * @param enterBackward: The transition applied to the slide when it enters the screen from the next slide
 *                       (user navigated backward).
 * @param exitForward: The transition applied to the slide when it exits the screen to the next slide
 *                     (user navigated forward).
 * @param exitBackward: The transition applied to the slide when it exits the screen to the previous slide
 *                      (user navigated backward).
 * @param modifier: A function that returns the modifier to be applied to the Slide container function of the
 *                  current transition.
 */
public class TransitionSet(
    public val enterForward: EnterTransition,
    public val enterBackward: EnterTransition,
    public val exitForward: ExitTransition,
    public val exitBackward: ExitTransition,
    public val modifier: @Composable AnimatedVisibilityScope.(Type) -> Modifier = { Modifier }
) {

    public enum class Type(public val isEnter: Boolean, public val isForward: Boolean) {
        /**
         * The transition is applied to the slide when it enters the screen from the previous slide
         * (user navigated forward).
         */
        EnterForward(true, true),
        /**
         * The transition is applied to the slide when it enters the screen from the next slide
         * (user navigated backward).
         */
        EnterBackward(true, false),
        /**
         * The transition is applied to the slide when it exits the screen to the next slide
         * (user navigated forward).
         */
        ExitForward(false, true),
        /**
         * The transition is applied to the slide when it exits the screen to the previous slide
         * (user navigated backward).
         */
        ExitBackward(false, false)
    }

    public companion object {
        private fun <T> defaultSpec(): FiniteAnimationSpec<T> = tween(600)

        public fun moveHorizontal(layoutDirection: LayoutDirection): TransitionSet {
            val dir = when (layoutDirection) {
                LayoutDirection.Ltr -> 1
                LayoutDirection.Rtl -> -1
            }
            return TransitionSet(
                enterForward = fadeIn(defaultSpec()) + slideIn(defaultSpec()) { IntOffset((it.width * 0.3).toInt() * dir, 0) },
                enterBackward = fadeIn(defaultSpec()) + slideIn(defaultSpec()) { IntOffset(-(it.width * 0.3).toInt() * dir, 0) },
                exitForward = fadeOut(defaultSpec()) + slideOut(defaultSpec()) { IntOffset(-(it.width * 0.3).toInt() * dir, 0) },
                exitBackward = fadeOut(defaultSpec()) + slideOut(defaultSpec()) { IntOffset((it.width * 0.3).toInt() * dir, 0) },
            )
        }

        public val moveVertical: TransitionSet = TransitionSet(
            enterForward = fadeIn(defaultSpec()) + slideIn(defaultSpec()) { IntOffset(0, (it.height * 0.3).toInt()) },
            enterBackward = fadeIn(defaultSpec()) + slideIn(defaultSpec()) { IntOffset(0, -(it.height * 0.3).toInt()) },
            exitForward = fadeOut(defaultSpec()) + slideOut(defaultSpec()) { IntOffset(0, -(it.height * 0.3).toInt()) },
            exitBackward = fadeOut(defaultSpec()) + slideOut(defaultSpec()) { IntOffset(0, (it.height * 0.3).toInt()) },
        )

        public val fade: TransitionSet = TransitionSet(
            enterForward = fadeIn(),
            enterBackward = fadeIn(),
            exitForward = fadeOut(),
            exitBackward = fadeOut(),
        )
    }
}
