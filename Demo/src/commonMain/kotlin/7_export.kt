import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material3.Text
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import net.kodein.cup.Slide
import utils.InlineIcon
import utils.Title


val export by Slide {
    Title {
        Text("You can export your presentation\nas multiple PNGs and/or as a PDF.")
    }

    Text(
        text = buildAnnotatedString {
            append("(Open the top left ")
            appendInlineContent("menu")
            append(" shown when moving your mouse,")
            appendLine()
            append("then click on ")
            appendInlineContent("export")
            append(" Export.)")
        },
        inlineContent = mapOf(
            "menu" to InlineIcon(Icons.Rounded.MoreVert, "menu"),
            "export" to InlineIcon(Icons.Rounded.PhotoCamera, "export"),
        ),
        textAlign = TextAlign.Center,
    )

}
