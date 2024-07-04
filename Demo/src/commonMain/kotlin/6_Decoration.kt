import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.kodein.emoji.Emoji
import org.kodein.emoji.compose.NotoAnimatedEmoji
import org.kodein.emoji.smileys_emotion.face_affection.StarStruck
import org.kodein.emoji.symbols.arrow.DownArrow
import net.kodein.cup.Slide
import utils.TextWithEmoji
import utils.Title


val decoration by Slide(
    stepCount = 2
) { step ->
    Title {
        Text("You can decorate and\ntheme your presentation")
    }
    Text(
        text = "This presentation uses the KODEIN theme that\nwe created for our own presentations!",
        textAlign = TextAlign.Center,
    )
    Spacer(Modifier.height(32.dp))
    AnimatedVisibility(step >= 1) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextWithEmoji("For example the ${Emoji.DownArrow} light purple bar is not standard and part of our theming.")
            Spacer(Modifier.height(16.dp))
            NotoAnimatedEmoji(Emoji.StarStruck, Modifier.size(64.dp))
        }
    }
}
