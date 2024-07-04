import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.ZoomOut
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.kodein.cup.Slide
import net.kodein.cup.SlideGroup
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

expect val modes: SlideGroup
