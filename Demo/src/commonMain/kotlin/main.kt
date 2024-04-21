import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import cup_demo.generated.resources.Res
import cup_demo.generated.resources.logo
import net.kodein.cup.*
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import net.kodein.cup.laser.laser
import net.kodein.cup.speaker.speakerMode
import net.kodein.cup.utils.DataMap
import net.kodein.cup.utils.DataMapElement
import org.kodein.emoji.compose.EmojiService


@Composable
fun KodeinPresentationPreview(
    slide: Slide,
    step: Int = slide.lastStep,
) {
    PresentationPreview(slide, step) { slides ->
        KodeinPresentation(slides)
    }
}

data class KodeinPresentationBackground(
    val color: Color,
) : DataMapElement<KodeinPresentationBackground>(Key) {

    companion object Key : DataMap.Key<KodeinPresentationBackground>
}

@Composable
private fun BoxScope.ProgressBar(presentationState: PresentationState) {
    val totalStepCount = presentationState.slides.sumOf { it.stepCount }
    val currentStepCount = presentationState.slides.subList(0, presentationState.currentSlideIndex)
        .sumOf { it.stepCount } + presentationState.currentStep
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .align(Alignment.BottomCenter)
    ) {
        val fraction by animateFloatAsState(
            targetValue = currentStepCount.toFloat() / (totalStepCount - 1).toFloat(),
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

@OptIn(ExperimentalResourceApi::class)
@Composable
fun KodeinPresentation(
    slides: SlideGroup
) {
    Presentation(
        slides = slides,
        configuration = {
            speakerMode()
            laser()
        },
        backgroundColor = KodeinTheme.Color.background
    ) {
        val presentationState = LocalPresentationState.current
        Image(
            painter = painterResource(Res.drawable.logo),
            contentDescription = null,
            alignment = Alignment.CenterEnd,
            colorFilter = ColorFilter.tint(KodeinTheme.Color.Dark),
            modifier = Modifier
                .alpha(0.4f)
                .fillMaxSize()
                .offset(x = (-16).dp, y = 64.dp)
        )
        val background by animateColorAsState(
            targetValue = presentationState.currentSlide.user[KodeinPresentationBackground]?.color ?: Color.Transparent,
            animationSpec = tween(1_500)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(background)
        ) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides KodeinTheme.Color.Light,
                    LocalTextStyle provides TextStyle(
                        fontFamily = KodeinTheme.Fonts.LCTPicon.Regular
                    )
                ) {
                    this@Presentation.Slides()
                }
            }

            ProgressBar(presentationState)
        }
    }
}

private val slides = Slides(
    intro,
    navigation,
    steps,
    transitions,
    sourceCode,
    modes,
    decoration,
    kodeinKoders,
    getStarted,
)

fun main() = cupApplication(
    title = "Presentation Demo",
) {
    remember { EmojiService.initialize() }
    KodeinPresentation(slides)
}
