package utils

import KodeinTheme
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import net.kodein.cup.LocalPresentationState
import net.kodein.cup.totalStepCount
import net.kodein.cup.totalStepCurrent
import net.kodein.cup.totalStepLast

@Composable
fun PresentationProgressBar(
    modifier: Modifier = Modifier
) {
    val presentationState = LocalPresentationState.current
    Box(
        modifier = modifier
    ) {
        val fraction by animateFloatAsState(
            targetValue = presentationState.totalStepCurrent.toFloat() / presentationState.totalStepLast.toFloat(),
            animationSpec = tween(300, easing = LinearOutSlowInEasing)
        )
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction = fraction)
                .align(Alignment.CenterStart)
                .background(KodeinTheme.Color.Dark)
        )
    }
}