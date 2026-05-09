import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextAlign
import net.kodein.cup.Slide
import net.kodein.cup.ui.styled
import utils.InlineIcon
import utils.Title


val export by Slide {
    Title {
        Text("You can export your presentation\nas multiple PNGs and/or as a PDF.")
    }

    Text(
        text = styled { "(Open the top left ${IC("menu")} shown when moving your mouse,\nthen click on ${IC("export")} Export.)" },
        inlineContent = mapOf(
            "menu" to InlineIcon(Icons.Rounded.MoreVert, "menu"),
            "export" to InlineIcon(Icons.Rounded.PhotoCamera, "export"),
        ),
        textAlign = TextAlign.Center,
    )

}
