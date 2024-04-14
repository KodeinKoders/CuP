import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.KeyboardReturn
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.SpaceBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.kodein.emoji.Emoji
import org.kodein.emoji.compose.NotoAnimatedEmoji
import org.kodein.emoji.people_body.hand_fingers_open.WavingHand
import net.kodein.cup.Slide
import net.kodein.cup.ui.styled
import utils.InlineIcon
import utils.Title


val intro by Slide {
    Title {
        SelectionContainer {
            Text("This is Compose UrPres!")
        }
    }

    NotoAnimatedEmoji(Emoji.WavingHand, Modifier.size(56.dp))

    Text(
        text = styled { "${+b}To advance the presentation:${-b} type ${IC("forward")}, ${IC("downward")}, ${IC("spacebar")}, or ${IC("return")}." },
        inlineContent = mapOf(
            "forward" to InlineIcon(Icons.AutoMirrored.Outlined.ArrowForward, "Arrow forward"),
            "downward" to InlineIcon(Icons.Outlined.ArrowDownward, "Arrow downward"),
            "spacebar" to InlineIcon(Icons.Outlined.SpaceBar, "Space bar"),
            "return" to InlineIcon(Icons.AutoMirrored.Outlined.KeyboardReturn, "Return"),
        )
    )
}
