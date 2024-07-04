import androidx.compose.material.Text
import net.kodein.cup.Slide
import net.kodein.cup.SlideGroup
import net.kodein.cup.Slides
import utils.Title


val speakerWindow by Slide(
    user = KodeinBanner(visible = true)
) {
    Title {
        Text("Desktop offers a Speaker Window")
    }
    Text("(Not available on Web exports.)")
}

actual val modes: SlideGroup = Slides(
    overview,
    speakerWindow
)
