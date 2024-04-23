import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.automirrored.rounded.SpeakerNotes
import androidx.compose.material.icons.rounded.ZoomOut
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import net.kodein.cup.Slide
import net.kodein.cup.Slides
import net.kodein.cup.TransitionSet
import net.kodein.cup.copyWithInsideTransitions
import net.kodein.cup.speaker.SpeakerNotesMD
import utils.InlineIcon
import utils.Title


val overview by Slide {
    Title {
        Text("Hit Escape to toggle Overview mode!")
    }
    Text(
        text = buildAnnotatedString {
            append("(Or click on the top left ")
            appendInlineContent("icon")
            append(" shown when moving your mouse.)")
        },
        inlineContent = mapOf(
            "icon" to InlineIcon(Icons.Rounded.ZoomOut, "icon")
        ),
        textAlign = TextAlign.Center,
    )
    Spacer(Modifier.height(16.dp))
    Text(
        text = buildAnnotatedString {
            append("You can also open the Slide & State List by clicking on the top right ")
            appendInlineContent("icon")
            append(".")
        },
        inlineContent = mapOf(
            "icon" to InlineIcon(Icons.AutoMirrored.Rounded.List, "icon")
        ),
        textAlign = TextAlign.Center,
    )
}

val speakerWindow by Slide(
    user = SpeakerNotesMD("""
            This slides has some **speaker notes** that you can see here!
            
            - You can use markdown in speaker notes
            - Or you can use regular compose if you prefer
            
            Isn't that cool!
    """)
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
    Spacer(Modifier.height(16.dp))
    Text(
        text = "Only available on desktop (not web).",
        fontSize = 0.6.em,
        textAlign = TextAlign.Center,
    )
}

val groups by Slide {
    Title {
        Text("You can group slides and apply specifics to all slides in the group")
    }
    Text(
        text = "For example, the last 3 slides are grouped and given a vertical transitions between them!",
        textAlign = TextAlign.Center,
    )
}

val modes = Slides(
    overview,
    speakerWindow,
    groups,
    specs = {
        copyWithInsideTransitions(
            config = it,
            startTransitions = TransitionSet.moveVertical,
            endTransitions = TransitionSet.moveVertical
        )
    }
)
