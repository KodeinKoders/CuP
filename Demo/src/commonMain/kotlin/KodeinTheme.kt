import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import cup_demo.generated.resources.*
import net.kodein.cup.sa.SourceCodeTheme
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font

object KodeinTheme {

    object Color {
        val background = Color(0xFF_230720)
        val Dark = Color(0xFF_480F40)
        val Light = Color(0xFF_F7E1DE)
        val BackgroundSpecial = Color(0xFF_480F40)
        val Orange = Color(0xFF_E8441F)
        val Link = Color(0xFF_EC755B)
    }

    @OptIn(ExperimentalResourceApi::class)
    object Fonts {
        val JetBrainsMono @Composable get() = FontFamily(
            Font(resource = Res.font.JetBrainsMono_Thin, weight = FontWeight.Thin, style = FontStyle.Normal),
            Font(resource = Res.font.JetBrainsMono_ThinItalic, weight = FontWeight.Thin, style = FontStyle.Italic),
            Font(resource = Res.font.JetBrainsMono_ExtraLight, weight = FontWeight.ExtraLight, style = FontStyle.Normal),
            Font(resource = Res.font.JetBrainsMono_ExtraLightItalic, weight = FontWeight.ExtraLight, style = FontStyle.Italic),
            Font(resource = Res.font.JetBrainsMono_Light, weight = FontWeight.Light, style = FontStyle.Normal),
            Font(resource = Res.font.JetBrainsMono_LightItalic, weight = FontWeight.Light, style = FontStyle.Italic),
            Font(resource = Res.font.JetBrainsMono_Regular, weight = FontWeight.Normal, style = FontStyle.Normal),
            Font(resource = Res.font.JetBrainsMono_Italic, weight = FontWeight.Normal, style = FontStyle.Italic),
            Font(resource = Res.font.JetBrainsMono_Medium, weight = FontWeight.Medium, style = FontStyle.Normal),
            Font(resource = Res.font.JetBrainsMono_MediumItalic, weight = FontWeight.Medium, style = FontStyle.Italic),
            Font(resource = Res.font.JetBrainsMono_SemiBold, weight = FontWeight.SemiBold, style = FontStyle.Normal),
            Font(resource = Res.font.JetBrainsMono_SemiBoldItalic, weight = FontWeight.SemiBold, style = FontStyle.Italic),
            Font(resource = Res.font.JetBrainsMono_Bold, weight = FontWeight.Bold, style = FontStyle.Normal),
            Font(resource = Res.font.JetBrainsMono_BoldItalic, weight = FontWeight.Bold, style = FontStyle.Italic),
            Font(resource = Res.font.JetBrainsMono_ExtraBold, weight = FontWeight.ExtraBold, style = FontStyle.Normal),
            Font(resource = Res.font.JetBrainsMono_ExtraBoldItalic, weight = FontWeight.ExtraBold, style = FontStyle.Italic),
        )

        object LCTPicon {
            val Regular @Composable get() = FontFamily(
                Font(resource = Res.font.LCTPicon_Regular_Thin, weight = FontWeight.Thin),
                Font(resource = Res.font.LCTPicon_Regular_ExtraLight, weight = FontWeight.ExtraLight),
                Font(resource = Res.font.LCTPicon_Regular_Light, weight = FontWeight.Light),
                Font(resource = Res.font.LCTPicon_Regular_Normal, weight = FontWeight.Normal),
                Font(resource = Res.font.LCTPicon_Regular_Medium, weight = FontWeight.Medium),
                Font(resource = Res.font.LCTPicon_Regular_SemiBold, weight = FontWeight.SemiBold),
                Font(resource = Res.font.LCTPicon_Regular_Bold, weight = FontWeight.Bold),
                Font(resource = Res.font.LCTPicon_Regular_ExtraBold, weight = FontWeight.ExtraBold),
                Font(resource = Res.font.LCTPicon_Regular_Black, weight = FontWeight.Black),
            )

            val Condensed @Composable get() = FontFamily(
                Font(resource = Res.font.LCTPicon_Condensed_Thin, weight = FontWeight.Thin),
                Font(resource = Res.font.LCTPicon_Condensed_ExtraLight, weight = FontWeight.ExtraLight),
                Font(resource = Res.font.LCTPicon_Condensed_Light, weight = FontWeight.Light),
                Font(resource = Res.font.LCTPicon_Condensed_Normal, weight = FontWeight.Normal),
                Font(resource = Res.font.LCTPicon_Condensed_Medium, weight = FontWeight.Medium),
                Font(resource = Res.font.LCTPicon_Condensed_SemiBold, weight = FontWeight.SemiBold),
                Font(resource = Res.font.LCTPicon_Condensed_Bold, weight = FontWeight.Bold),
                Font(resource = Res.font.LCTPicon_Condensed_ExtraBold, weight = FontWeight.ExtraBold),
                Font(resource = Res.font.LCTPicon_Condensed_Black, weight = FontWeight.Black),
            )

            val Extended @Composable get() = FontFamily(
                Font(resource = Res.font.LCTPicon_Extended_Thin, weight = FontWeight.Thin),
                Font(resource = Res.font.LCTPicon_Extended_ExtraLight, weight = FontWeight.ExtraLight),
                Font(resource = Res.font.LCTPicon_Extended_Light, weight = FontWeight.Light),
                Font(resource = Res.font.LCTPicon_Extended_Normal, weight = FontWeight.Normal),
                Font(resource = Res.font.LCTPicon_Extended_Medium, weight = FontWeight.Medium),
                Font(resource = Res.font.LCTPicon_Extended_SemiBold, weight = FontWeight.SemiBold),
                Font(resource = Res.font.LCTPicon_Extended_Bold, weight = FontWeight.Bold),
                Font(resource = Res.font.LCTPicon_Extended_ExtraBold, weight = FontWeight.ExtraBold),
                Font(resource = Res.font.LCTPicon_Extended_Black, weight = FontWeight.Black),
            )
        }
    }

    val SourceCodeTheme: SourceCodeTheme = { cls ->
        when (cls) {
            "default"
                -> SpanStyle(
                    color = Color(0xFF_F7E1DE)
                )

            "code",
            "selector-class",
            "subst",
            "type",
            "built_in",
            "builtin-name",
            "symbol",
            "selector-id",
            "selector-attr",
            "selector-pseudo",
            "template-tag",
            "template-variable",
            "addition",
            "title",
                -> SpanStyle(
                    color = Color(0xFF_F0A698)
                )

            "keyword",
            "selector-tag",
            "section",
            "attribute",
            "name",
            "variable",
                -> SpanStyle(
                    color = Color(0xFF_EC755B)
                )

            "bullet",
            "quote",
            "link",
            "number",
            "regexp",
            "literal",
            "string",
                -> SpanStyle(
                    color = Color(0xFF_D39AB8)
                )

            "comment",
            "deletion",
            "meta",
                -> SpanStyle(
                    color = Color(0xFF_B35C9D)
                )

            "strong",
                -> SpanStyle(
                    color = Color(0xFF_F0A698),
                    fontWeight = FontWeight.Bold
                )

            "emphasis",
                -> SpanStyle(
                    color = Color(0xFF_F0A698),
                    fontStyle = FontStyle.Italic
                )

            else -> null
        }
    }
}
