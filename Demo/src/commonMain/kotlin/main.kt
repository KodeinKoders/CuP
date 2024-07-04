import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cup_demo.generated.resources.Res
import cup_demo.generated.resources.logo
import net.kodein.cup.*
import net.kodein.cup.laser.laser
import net.kodein.cup.speaker.speakerWindow
import net.kodein.cup.utils.DataMap
import net.kodein.cup.utils.DataMapElement
import org.jetbrains.compose.resources.painterResource
import org.kodein.emoji.compose.EmojiService
import utils.PresentationProgressBar


@Composable
fun KodeinPresentationPreview(
    slide: Slide,
    step: Int = slide.lastStep,
) {
    PresentationPreview(slide, step) { slides ->
        KodeinPresentation(slides)
    }
}

data class KodeinBackground(
    val color: Color,
) : DataMapElement<KodeinBackground>(Key) {
    companion object Key : DataMap.Key<KodeinBackground>
}

data class KodeinBanner(
    val visible: Boolean,
) : DataMapElement<KodeinBanner>(Key) {
    companion object Key : DataMap.Key<KodeinBanner>
}

@Composable
fun KodeinPresentation(
    slides: SlideGroup
) {

    Presentation(
        slides = slides,
        configuration = {
            speakerWindow()
            laser()
        },
        backgroundColor = KodeinTheme.Color.background
    ) { slidesContent ->
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
            targetValue = presentationState.currentSlide.user[KodeinBackground]?.color ?: Color.Transparent,
            animationSpec = tween(1_500)
        )
        val density = LocalDensity.current
        val bannerAlpha by animateFloatAsState(
            targetValue = if (presentationState.currentSlide.user[KodeinBanner]?.visible == true) 1f else 0f,
            animationSpec = tween(1_000)
        )
        if (bannerAlpha > 0f) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .graphicsLayer(
                        rotationZ = 45f,
                        translationY = with(density) { (-16 + 64).dp.toPx() },
                        translationX = with(density) { (160 - 64).dp.toPx() },
                        alpha = 0.5f * bannerAlpha
                    )
                    .size(width = 320.dp, height = 32.dp)
                    .background(KodeinTheme.Color.Orange)
                    .align(Alignment.TopEnd)
            ) {
                Text(
                    text = "Desktop only!",
                    fontSize = 20.sp,
                    color = Color.Black,
                )
            }
        }

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
                    ),
                ) {
                    slidesContent()
                }
            }

            PresentationProgressBar(
                Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .align(Alignment.BottomCenter)
            )
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
