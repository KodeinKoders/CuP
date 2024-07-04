import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import net.kodein.cup.PreparedSlide
import net.kodein.cup.sa.SAStyle
import net.kodein.cup.sa.SourceCode
import net.kodein.cup.sa.line
import net.kodein.cup.sa.rememberSourceCode
import net.kodein.cup.ui.SpanStyleSheet
import net.kodein.cup.ui.styled
import utils.Title


val sourceCode by PreparedSlide(
    stepCount = 9
) {
    @Suppress("LocalVariableName", "RemoveCurlyBracesFromTemplate")
    val sourceCode = rememberSourceCode("kotlin") {
        val errorStyle = SAStyle.line(Color.Red, squiggle = true)

        val CmRecomputes by marker(onlyShown(3))
        val Function by marker(hidden(0))
        val CmHardWork by marker(highlighted(2))
        val CmOnlyOnce by marker(onlyShown(8))
        val Get by marker(onlyShown(0..3), highlighted(2))
        val Equal by marker(onlyShown(0..5))
        val Lazy by marker(hidden(0..3))
        val LazyH by marker(highlighted(7))
        val By by marker(hidden(0..5))
        val Error by marker(styled(errorStyle, 5))

        """
                class Universe {
                ${CmRecomputes}    // Recomputes every time!${X}
                    val answer: ${Error}Int ${Get}get() ${X}${Equal}=${X}${Lazy}${LazyH}${By}by${X} lazy${X} {${X} computeAnswer()${Lazy} }${X}${X}
                    private fun computeAnswer(): Int {
                ${Function}        ${CmHardWork}// Hard work${CmOnlyOnce} computed only once!${X}${X}
                        println("Computing...")
                        Thread.sleep(2_000)
                        return 42
                ${X}    }
                }
            """.trimIndent()
    }

    slideContent { step ->
        val stylesheet = object : SpanStyleSheet() {
            val pre by registerMarker(SpanStyle(
                fontFamily = KodeinTheme.Fonts.JetBrainsMono,
                color = KodeinTheme.Color.Orange
            ))
        }

        Title {
            Text("You can animate source code!")
        }
        Text(styled(stylesheet) { "(Here's an example of how I would introduce Kotlin's ${+pre}lazy${-pre} property delegate)" })
        Spacer(Modifier.height(8.dp))
        SourceCode(
            sourceCode = sourceCode,
            step = step,
            style = TextStyle(fontFamily = KodeinTheme.Fonts.JetBrainsMono),
            theme = KodeinTheme.SourceCodeTheme,
            modifier = Modifier
                .background(Color.DarkGray, RoundedCornerShape(4.dp))
                .padding(8.dp)
        )
        Spacer(Modifier.height(8.dp))
        Row {
            Text(" ")
            AnimatedVisibility(step >= 1) { Text("You can reveal") }
            AnimatedVisibility(step >= 2) { Text(", highlight") }
            AnimatedVisibility(step >= 4) { Text(", remove, replace") }
            AnimatedVisibility(step >= 5) { Text(" or decorate source code") }
            Text(" ")
        }
    }
}
