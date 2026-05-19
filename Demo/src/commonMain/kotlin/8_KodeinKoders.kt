import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.takeOrElse
import cup_demo.generated.resources.Res
import cup_demo.generated.resources.logo
import net.kodein.cup.SLIDE_SIZE_16_9
import net.kodein.cup.Slide
import net.kodein.cup.SlideSpecs
import net.kodein.cup.isShiftPressed
import net.kodein.cup.key
import net.kodein.cup.keyevents.CupKeyEventEffect
import net.kodein.cup.type
import org.jetbrains.compose.resources.vectorResource


val kodeinKoders by Slide(
    specs = SlideSpecs(size = SLIDE_SIZE_16_9)
) {
    val scale by rememberInfiniteTransition().animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(2_500), RepeatMode.Reverse)
    )

    Text(
        text = "CuP is brought to you by:",
        modifier = Modifier.padding(bottom = 16.dp)
    )

    var showLines by remember { mutableStateOf(false) }
    CupKeyEventEffect {
        if (it.type == KeyEventType.KeyDown && it.key == Key.R && it.isShiftPressed) {
            showLines = !showLines
            true
        } else false
    }

    val uriHandler = LocalUriHandler.current

    KodeinLogo(
        division = "Koders",
        subtext = { Text("Kotlin Multiplatform Experts") },
        mainFontSize = 32.sp,
        showDesignLines = showLines,
        modifier = Modifier
            .scale(scale)
            .pointerHoverIcon(PointerIcon.Hand)
            .clickable {
                uriHandler.openUri("https://kodein.net")
            }
    )
}

@Composable
fun KodeinLogo(
    division: String,
    modifier: Modifier = Modifier,
    mainFontSize: TextUnit = TextUnit.Unspecified,
    logoColor: Color = KodeinTheme.Colors.orange600,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    showDesignLines: Boolean = false,
    subtext: @Composable () -> Unit
) {
    val lineColor = lerp(textColor, logoColor, 0.5f).copy(alpha = 0.8f)

    val fontSize = mainFontSize.takeOrElse { LocalTextStyle.current.fontSize }

    val logoHeightInFontSize = 2.775514f
    val logoWidthInHeight = 0.7946182f
    val spaceWidthInFontSize = 0.94f
    val circleSizeInHeight = 0.25718683f
    val firstCircleTopInHeight = 0.19210806f
    val secondCircleTopInHeight = 0.55059946f

    val density = LocalDensity.current
    val logoHeight = with(density) { (fontSize.toPx() * logoHeightInFontSize).toDp() }
    val logoWidth = logoHeight * logoWidthInHeight
    val spaceWith = with(density) { (fontSize.toPx() * spaceWidthInFontSize).toDp() }
    val firstTextTop = logoHeight * firstCircleTopInHeight
    val secondTextTop = logoHeight * secondCircleTopInHeight

    Box(
        modifier = modifier
            .height(logoHeight)
            .drawWithContent {
                drawContent()
                if (showDesignLines) {
                    listOf(
                        firstCircleTopInHeight,
                        firstCircleTopInHeight + circleSizeInHeight,
                        secondCircleTopInHeight,
                        secondCircleTopInHeight + circleSizeInHeight,
                    ).forEach {
                        drawLine(
                            color = lineColor,
                            start = Offset(0f, size.height * it),
                            end = Offset(size.width, size.height * it)
                        )
                    }
                    drawLine(
                        color = lineColor,
                        start = Offset(size.height * 0.7946182f, 0f),
                        end = Offset(size.height * 0.7946182f, size.height)
                    )
                    drawLine(
                        color = lineColor,
                        start = Offset(size.height * 0.7946182f + spaceWith.toPx(), 0f),
                        end = Offset(size.height * 0.7946182f + spaceWith.toPx(), size.height)
                    )
                }
            }
    ) {
        // Not using ProvideTextStyle because we want to fully ignore outside font configuration.
        CompositionLocalProvider(
            LocalTextStyle provides TextStyle(
                fontFamily = KodeinTheme.Fonts.LCTPicon.Regular,
                fontWeight = FontWeight.Black,
                fontSize = fontSize,
                lineHeight = fontSize,
            ),
            LocalContentColor provides textColor,
        ) {
            Image(
                imageVector = vectorResource(Res.drawable.logo),
                contentDescription = null,
                colorFilter = ColorFilter.tint(logoColor),
                modifier = Modifier
                    .fillMaxHeight()
            )
            if (showDesignLines) {
                Text(
                    text = "m",
                    fontWeight = FontWeight.Black,
                    fontSize = fontSize,
                    color = lineColor,
                    modifier = Modifier
                        .padding(
                            start = logoWidth - with(density) { (fontSize.toPx() * 0.04f).toDp() },
                        )
                        .graphicsLayer {
                            translationY = -fontSize.toPx() * 0.34f
                            alpha = .6f
                        }
                )
            }
            Text(
                text = "Kodein $division",
                color = textColor,
                fontWeight = FontWeight.Black,
                fontSize = fontSize,
                lineHeight = fontSize * 0.6,
                modifier = Modifier
                    .padding(
                        start = logoWidth + spaceWith - with(density) { (fontSize.toPx() * 0.04f).toDp() },
                        top = firstTextTop - with(density) { (fontSize.toPx() * 0.16f).toDp() },
                    )
            )
            Box(
                modifier = Modifier
                    .padding(
                        start = logoWidth + spaceWith - with(density) { (fontSize.toPx() * 0.04f).toDp() },
                        top = secondTextTop,
                    )
            ) {
                ProvideTextStyle(
                    TextStyle(
                        fontSize = fontSize * .7f,
                        lineHeight = fontSize * .7f,
                        fontWeight = FontWeight.Normal,
                    )
                ) {
                    subtext()
                }
            }
        }
    }
}
