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
    public val enter: (Boolean) -> EnterTransition,
    public val exit: (Boolean) -> ExitTransition,
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
                enter = { isForward ->
                    fadeIn(defaultSpec()) + slideIn(defaultSpec()) {
                        if (isForward) IntOffset((it.width * 0.3).toInt() * dir, 0)
                        else IntOffset(-(it.width * 0.35).toInt() * dir, 0)
                    }
                },
                exit = { isForward ->
                    fadeOut(defaultSpec()) + slideOut(defaultSpec()) {
                        if (isForward) IntOffset(-(it.width * 0.3).toInt() * dir, 0)
                        else IntOffset((it.width * 0.3).toInt() * dir, 0)
                    }
                }
            )
        }

        public val moveVertical: TransitionSet = TransitionSet(
            enter = { isForward ->
                fadeIn(defaultSpec()) + slideIn(defaultSpec()) {
                    if (isForward) IntOffset(0, (it.height * 0.3).toInt())
                    else IntOffset(0, -(it.height * 0.3).toInt())
                }
            },
            exit = { isForward ->
                fadeOut(defaultSpec()) + slideOut(defaultSpec()) {
                    if (isForward) IntOffset(0, -(it.height * 0.3).toInt())
                    else IntOffset(0, (it.height * 0.3).toInt())
                }
            }
        )

        public val fade: TransitionSet = TransitionSet(
            enter = { fadeIn() },
            exit = { fadeOut() }
        )
    }
}
