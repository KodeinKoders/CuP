import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.kodein.cup.Slide
import net.kodein.cup.SlideSpecs
import net.kodein.cup.automove.AutoMovePause
import net.kodein.cup.imgexp.Export
import net.kodein.cup.utils.slideContextOf
import utils.TextWithEmoji
import utils.Title
import utils.y3DRotation
import kotlin.time.Duration.Companion.seconds


val transitions by Slide(
    stepCount = 2,
    specs = SlideSpecs(startTransitions = y3DRotation),
    context = slideContextOf(
        KodeinBackground(KodeinTheme.Color.BackgroundSpecial),
        AutoMovePause.onSteps(0..0) { 2.seconds },
        Export.only(1)
    ),
) { step ->
    Title(Modifier.padding(16.dp)) {
        Text("There can also be complex slide transition transitions !")
    }
    AnimatedVisibility(step >= 1) {
        TextWithEmoji("Have you noticed the background change? 🤔")
    }
}
