import androidx.compose.foundation.clickable
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import net.kodein.cup.Slide
import utils.Title


val getStarted by Slide {
    Title {
        Text("Get Started!")
    }
    val uriHandler = LocalUriHandler.current
    Text(
        text = "https://github.com/kosi-libs/CuP",
        textAlign = TextAlign.Center,
        color = KodeinTheme.Color.Link,
        modifier = Modifier
            .pointerHoverIcon(PointerIcon.Hand)
            .clickable {
                uriHandler.openUri("https://github.com/kosi-libs/CuP")
            }
    )
}
