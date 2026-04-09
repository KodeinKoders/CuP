import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import net.kodein.cup.Slide
import net.kodein.cup.isShiftPressed
import net.kodein.cup.key
import net.kodein.cup.keyevents.CupKeyEventEffect
import net.kodein.cup.type
import utils.Title


val getStarted by Slide {

    var showSecret by remember { mutableStateOf(false) }

    CupKeyEventEffect {
        if (it.type == KeyEventType.KeyDown && it.key == Key.R && it.isShiftPressed) {
            showSecret = true
            true
        } else false
    }

    Title {
        Text("Get Started!")
    }
    val uriHandler = LocalUriHandler.current
    Text(
        text = "https://github.com/KodeinKoders/CuP",
        textAlign = TextAlign.Center,
        color = KodeinTheme.Color.Link,
        modifier = Modifier
            .pointerHoverIcon(PointerIcon.Hand)
            .clickable {
                uriHandler.openUri("https://github.com/KodeinKoders/CuP")
            }
    )

    AnimatedVisibility(visible = showSecret) {
        Column {
            Spacer(Modifier.height(32.dp))
            Text(
                text = buildAnnotatedString {
                    appendLine("Hey, you found our secret!")
                    appendLine("If you ever publish a presentation using CuP,")
                    append("send us an e-mail at ")
                    withLink(
                        LinkAnnotation.Url(
                            url = "mailto:contact@kodein.net",
                            styles = TextLinkStyles(
                                style = SpanStyle(color = KodeinTheme.Color.Link)
                            )
                        )
                    ) {
                        append("contact@kodein.net")
                    }
                    appendLine(",")
                    appendLine("and we'll repost your social media posts about it!")
                },
                textAlign = TextAlign.Center,
            )
        }
    }
}
