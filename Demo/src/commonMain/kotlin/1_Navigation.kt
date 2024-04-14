import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.kodein.emoji.Emoji
import org.kodein.emoji.smileys_emotion.face_smiling.Wink
import net.kodein.cup.Slide
import net.kodein.cup.ui.styled
import utils.InlineIcon
import utils.TextWithEmoji


val navigation by Slide {
    Text(
        text = styled { "${+b}To move back:${-b} type ${IC("backward")}, ${IC("upward")}, or ${IC("backspace")}." },
        inlineContent = mapOf(
            "backward" to InlineIcon(Icons.AutoMirrored.Outlined.ArrowBack, "Arrow backward"),
            "upward" to InlineIcon(Icons.Outlined.ArrowUpward, "Arrow upward"),
            "backspace" to InlineIcon(Icons.AutoMirrored.Outlined.Backspace, "Backspace"),
        )
    )
    Spacer(Modifier.height(16.dp))
    TextWithEmoji("Still, move forward to see the rest of the features ${Emoji.Wink}")
}
