import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.SpeakerNotes
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import net.kodein.cup.*
import net.kodein.cup.speaker.SpeakerNotes
import net.kodein.cup.utils.dataMapOf
import utils.InlineIcon
import utils.Title


val speakerWindow by Slide(
    user = dataMapOf(
        SpeakerNotes(
            """
                    This slides has some **speaker notes** that you can see here!
                    
                    Speaker notes are written in _Markdown_.
                    Isn't that cool!
            """
        ),
        KodeinBanner(visible = true)
    )
) {
    Title {
        Text("Hit S to open the Speaker Window!")
    }
    Text(
        text = buildAnnotatedString {
            append("(Or click on the top left ")
            appendInlineContent("icon")
            append(" shown when moving your mouse.)")
        },
        inlineContent = mapOf(
            "icon" to InlineIcon(Icons.AutoMirrored.Rounded.SpeakerNotes, "icon")
        ),
        textAlign = TextAlign.Center,
    )
}

actual val modes: SlideGroup = Slides(
    overview,
    speakerWindow,
    specs = {
        it.insideTransitionSpecs(
            startTransitions = TransitionSet.moveVertical,
            endTransitions = TransitionSet.moveVertical
        ) + SlideSpecs(
            size = SLIDE_SIZE_16_9
        )
    }
)
