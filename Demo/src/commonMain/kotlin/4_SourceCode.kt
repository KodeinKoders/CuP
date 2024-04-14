import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import net.kodein.cup.Slide
import net.kodein.cup.sa.*
import utils.Title

val sourceCode by Slide(
    stepCount = 9,
    prepare = {
        sourceCode(sourceCodeDef)
    }
) { step ->
    Title {
        Text("You can animate source code!")
    }
    Box(
        Modifier
            .background(Color.DarkGray, RoundedCornerShape(4.dp))
            .padding(8.dp)
    ) {
        SourceCode(
            sourceCode = sourceCodeDef,
            step = step,
            style = TextStyle(fontFamily = KodeinTheme.Fonts.JetBrainsMono),
            theme = KodeinTheme.SourceCodeTheme
        )
    }
}

@Suppress("LocalVariableName", "RemoveCurlyBracesFromTemplate")
private val sourceCodeDef = SourceCode("kotlin") {
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
