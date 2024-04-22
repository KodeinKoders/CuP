import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cup_demo.generated.resources.Res
import cup_demo.generated.resources.logo
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import net.kodein.cup.SLIDE_SIZE_16_9
import net.kodein.cup.Slide


@OptIn(ExperimentalResourceApi::class)
val kodeinKoders by Slide(
    specs = { copy(size = SLIDE_SIZE_16_9) },
    user = KodeinBanner(visible = true)
) {
    Text(
        text = "CuP is brought to you by:",
        modifier = Modifier.padding(bottom = 16.dp)
    )

    val uriHandler = LocalUriHandler.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .pointerHoverIcon(PointerIcon.Hand)
            .clickable {
                uriHandler.openUri("https://kodein.net")
            }
    ) {
        Image(
            painter = painterResource(Res.drawable.logo),
            contentDescription = null,
            colorFilter = ColorFilter.tint(KodeinTheme.Color.Orange),
            modifier = Modifier.height(112.dp).padding(end = 12.dp)
        )
        Column {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("KODEIN")
                    }
                    withStyle(SpanStyle(fontWeight = FontWeight.Light)) {
                        append("Koders")
                    }
                },
                color = KodeinTheme.Color.Orange,
                fontSize = 56.sp,
                lineHeight = 56.sp,
                modifier = Modifier.height(46.dp)
            )
            Text(
                text = "painless multiplatform technology",
                color = KodeinTheme.Color.Orange,
                fontSize = 22.sp,
                modifier = Modifier.padding(start = 2.dp)
            )
        }
    }
}
