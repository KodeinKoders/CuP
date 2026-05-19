import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cup_demo.generated.resources.JetBrainsMono_Bold
import cup_demo.generated.resources.JetBrainsMono_BoldItalic
import cup_demo.generated.resources.JetBrainsMono_ExtraBold
import cup_demo.generated.resources.JetBrainsMono_ExtraBoldItalic
import cup_demo.generated.resources.JetBrainsMono_ExtraLight
import cup_demo.generated.resources.JetBrainsMono_ExtraLightItalic
import cup_demo.generated.resources.JetBrainsMono_Italic
import cup_demo.generated.resources.JetBrainsMono_Light
import cup_demo.generated.resources.JetBrainsMono_LightItalic
import cup_demo.generated.resources.JetBrainsMono_Medium
import cup_demo.generated.resources.JetBrainsMono_MediumItalic
import cup_demo.generated.resources.JetBrainsMono_Regular
import cup_demo.generated.resources.JetBrainsMono_SemiBold
import cup_demo.generated.resources.JetBrainsMono_SemiBoldItalic
import cup_demo.generated.resources.JetBrainsMono_Thin
import cup_demo.generated.resources.JetBrainsMono_ThinItalic
import cup_demo.generated.resources.LCTPicon_Regular_Black
import cup_demo.generated.resources.LCTPicon_Regular_Bold
import cup_demo.generated.resources.LCTPicon_Regular_ExtraBold
import cup_demo.generated.resources.LCTPicon_Regular_ExtraLight
import cup_demo.generated.resources.LCTPicon_Regular_Light
import cup_demo.generated.resources.LCTPicon_Regular_Medium
import cup_demo.generated.resources.LCTPicon_Regular_Normal
import cup_demo.generated.resources.LCTPicon_Regular_SemiBold
import cup_demo.generated.resources.LCTPicon_Regular_Thin
import cup_demo.generated.resources.Res
import net.kodein.cup.sa.SourceCodeTheme
import org.jetbrains.compose.resources.Font

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
object KodeinTheme {

    object Colors {
        val orange100: Color = Color(0xFF_FDDDD6)
        val orange200: Color = Color(0xFF_FAC0B0)
        val orange300: Color = Color(0xFF_F0A698)
        val orange400: Color = Color(0xFF_EC7A5C)
        val orange500: Color = Color(0xFF_EF5E36)
        val orange600: Color = Color(0xFF_E84420)

        val purple500: Color = Color(0xFF_7A2A71)
        val purple600: Color = Color(0xFF_480F40)
        val purple800: Color = Color(0xFF_250821)

        // Rare
        val orange050: Color = Color(0xFF_FEF2EF)
        val orange700: Color = Color(0xFF_C43417)
        val orange800: Color = Color(0xFF_9B2510)
        val orange900: Color = Color(0xFF_6E180A)
        val orange950: Color = Color(0xFF_4A0E05)
        val purple050: Color = Color(0xFF_F7EEF6)
        val purple100: Color = Color(0xFF_E8C7E3)
        val purple200: Color = Color(0xFF_D9A0CF)
        val purple300: Color = Color(0xFF_BE72B3)
        val purple400: Color = Color(0xFF_9E4994)
        val purple700: Color = Color(0xFF_360A30)
        val purple900: Color = Color(0xFF_11030E)
        val purple950: Color = Color(0xFF_090108)

        // Computed
        val orange075: Color = lerp(orange050, orange100, 0.5f)
        val orange125: Color = lerp(orange100, orange200, 0.25f)
        val orange150: Color = lerp(orange100, orange200, 0.5f)
        val orange175: Color = lerp(orange100, orange200, 0.75f)
        val orange225: Color = lerp(orange200, orange300, 0.25f)
        val orange250: Color = lerp(orange200, orange300, 0.5f)
        val orange275: Color = lerp(orange200, orange300, 0.75f)
        val orange325: Color = lerp(orange300, orange400, 0.25f)
        val orange350: Color = lerp(orange300, orange400, 0.5f)
        val orange375: Color = lerp(orange300, orange400, 0.75f)
        val orange425: Color = lerp(orange400, orange500, 0.25f)
        val orange450: Color = lerp(orange400, orange500, 0.5f)
        val orange475: Color = lerp(orange400, orange500, 0.75f)
        val orange525: Color = lerp(orange500, orange600, 0.25f)
        val orange550: Color = lerp(orange500, orange600, 0.5f)
        val orange575: Color = lerp(orange500, orange600, 0.75f)
        val orange625: Color = lerp(orange600, orange700, 0.25f)
        val orange650: Color = lerp(orange600, orange700, 0.5f)
        val orange675: Color = lerp(orange600, orange700, 0.25f)
        val orange725: Color = lerp(orange700, orange800, 0.25f)
        val orange750: Color = lerp(orange700, orange800, 0.5f)
        val orange775: Color = lerp(orange700, orange800, 0.75f)
        val orange825: Color = lerp(orange800, orange900, 0.25f)
        val orange850: Color = lerp(orange800, orange900, 0.5f)
        val orange875: Color = lerp(orange800, orange900, 0.75f)
        val orange925: Color = lerp(orange900, orange950, 0.5f)

        val purple075: Color = lerp(purple050, purple100, 0.5f)
        val purple125: Color = lerp(purple100, purple200, 0.25f)
        val purple150: Color = lerp(purple100, purple200, 0.5f)
        val purple175: Color = lerp(purple100, purple200, 0.75f)
        val purple225: Color = lerp(purple200, purple300, 0.25f)
        val purple250: Color = lerp(purple200, purple300, 0.5f)
        val purple275: Color = lerp(purple200, purple300, 0.75f)
        val purple325: Color = lerp(purple300, purple400, 0.25f)
        val purple350: Color = lerp(purple300, purple400, 0.5f)
        val purple375: Color = lerp(purple300, purple400, 0.75f)
        val purple425: Color = lerp(purple400, purple500, 0.25f)
        val purple450: Color = lerp(purple400, purple500, 0.5f)
        val purple475: Color = lerp(purple400, purple500, 0.75f)
        val purple525: Color = lerp(purple500, purple600, 0.25f)
        val purple550: Color = lerp(purple500, purple600, 0.5f)
        val purple575: Color = lerp(purple500, purple600, 0.75f)
        val purple625: Color = lerp(purple600, purple700, 0.25f)
        val purple650: Color = lerp(purple600, purple700, 0.5f)
        val purple675: Color = lerp(purple600, purple700, 0.75f)
        val purple725: Color = lerp(purple700, purple800, 0.25f)
        val purple750: Color = lerp(purple700, purple800, 0.5f)
        val purple775: Color = lerp(purple700, purple800, 0.75f)
        val purple825: Color = lerp(purple800, purple900, 0.25f)
        val purple850: Color = lerp(purple800, purple900, 0.5f)
        val purple875: Color = lerp(purple800, purple900, 0.75f)
        val purple925: Color = lerp(purple900, purple950, 0.5f)
    }

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
        }
    }

    val lightColorScheme: ColorScheme = ColorScheme(
        primary = Colors.orange600,
        onPrimary = Colors.orange050,
        primaryContainer = Colors.orange300,
        onPrimaryContainer = Colors.purple600,
        inversePrimary = Colors.orange300,
        secondary = Colors.orange800.copy(),
        onSecondary = Colors.orange050,
        secondaryContainer = Colors.orange250,
        onSecondaryContainer = Colors.purple800,
        tertiary = Colors.purple500,
        onTertiary = Colors.orange050,
        tertiaryContainer = Colors.purple100,
        onTertiaryContainer = Colors.purple600,
        background = Colors.orange100, // = surface
        onBackground = Colors.purple600, // = onSurface
        surface = Colors.orange100, // = background
        onSurface = Colors.purple600, // = onBackground
        surfaceVariant = Colors.purple100,
        onSurfaceVariant = Colors.purple600,
        surfaceTint = Colors.purple600, // = primary
        inverseSurface = Colors.orange900,
        inverseOnSurface = Colors.orange100,
        error = Color(0xFF_C00000),
        onError = Color(0xFF_FFFFFF),
        errorContainer = Color(0xFF_FFA0A0),
        onErrorContainer = Color(0xFF_300000),
        outline = Colors.purple600,
        outlineVariant = Colors.purple200,
        scrim = Colors.purple800,
        surfaceBright = Colors.orange100, // = surface
        surfaceDim = Colors.orange200,
        surfaceContainer = Colors.orange175,
        surfaceContainerHigh = Colors.orange200,
        surfaceContainerHighest = Colors.orange225,
        surfaceContainerLow = Colors.orange150,
        surfaceContainerLowest = Colors.orange125,
        primaryFixed = Colors.orange200,
        primaryFixedDim = Colors.orange300,
        onPrimaryFixed = Colors.purple600,
        onPrimaryFixedVariant = Colors.purple500,
        secondaryFixed = Colors.orange150,
        secondaryFixedDim = Colors.orange250,
        onSecondaryFixed = Colors.purple800,
        onSecondaryFixedVariant = Colors.purple600,
        tertiaryFixed = Colors.purple200,
        tertiaryFixedDim = Colors.purple250,
        onTertiaryFixed = Colors.purple800,
        onTertiaryFixedVariant = Colors.purple600,
    )

    val darkColorScheme: ColorScheme = ColorScheme(
        primary = Colors.orange300,
        onPrimary = Colors.purple600,
        primaryContainer = Colors.orange800,
        onPrimaryContainer = Colors.orange100,
        inversePrimary = Colors.orange900,
//        secondary = Colors.orange100,
//        onSecondary = Colors.orange800,
//        secondaryContainer = Colors.orange950,
//        onSecondaryContainer = Colors.orange200,
//        tertiary = Colors.purple200,
//        onTertiary = Colors.purple800,
//        tertiaryContainer = Colors.purple600,
//        onTertiaryContainer = Colors.purple100,
        secondary = Colors.purple200,
        onSecondary = Colors.purple800,
        secondaryContainer = Colors.purple600,
        onSecondaryContainer = Colors.orange100,
        tertiary = Colors.orange100,
        onTertiary = Colors.orange800,
        tertiaryContainer = Colors.orange950,
        onTertiaryContainer = Colors.orange100,
        background = Colors.purple800, // = surface
        onBackground = Colors.orange100, // = onSurface
        surface = Colors.purple800, // = background
        onSurface = Colors.orange100, // = onBackground
        surfaceVariant = Colors.orange950,
        onSurfaceVariant = Colors.purple100,
        surfaceTint = Colors.orange300, // = primary
        inverseSurface = Colors.purple100,
        inverseOnSurface = Colors.purple600,
        error = Color(0xFF_FFC0C0),
        onError = Color(0xFF_800000),
        errorContainer = Color(0xFF_C00000),
        onErrorContainer = Color(0xFF_FFE0E0),
        outline = Colors.purple300,
        outlineVariant = Colors.purple550,
        scrim = Colors.purple950,
        surfaceBright = Colors.purple600,
        surfaceDim = Colors.purple800,
        surfaceContainer = Colors.purple700,
        surfaceContainerHigh = Colors.purple650,
        surfaceContainerHighest = Colors.purple600,
        surfaceContainerLow = Colors.purple750,
        surfaceContainerLowest = Colors.purple850,
        primaryFixed = Colors.orange200,
        primaryFixedDim = Colors.orange300,
        onPrimaryFixed = Colors.purple600,
        onPrimaryFixedVariant = Colors.purple500,
//        secondaryFixed = Colors.orange150,
//        secondaryFixedDim = Colors.orange250,
//        onSecondaryFixed = Colors.purple800,
//        onSecondaryFixedVariant = Colors.purple600,
//        tertiaryFixed = Colors.purple200,
//        tertiaryFixedDim = Colors.purple250,
//        onTertiaryFixed = Colors.purple800,
//        onTertiaryFixedVariant = Colors.purple600,
        secondaryFixed = Colors.purple200,
        secondaryFixedDim = Colors.purple250,
        onSecondaryFixed = Colors.purple800,
        onSecondaryFixedVariant = Colors.purple600,
        tertiaryFixed = Colors.orange150,
        tertiaryFixedDim = Colors.orange250,
        onTertiaryFixed = Colors.purple800,
        onTertiaryFixedVariant = Colors.purple600,
    )

    val typography: Typography
        @Composable get() =
            Typography(
                displayLarge = TextStyle(
                    fontFamily = Fonts.LCTPicon.Regular,
                    fontWeight = FontWeight.Black,
                    fontSize = 57.sp,
                    lineHeight = 57.sp,
                ),
                displayMedium = TextStyle(
                    fontFamily = Fonts.LCTPicon.Regular,
                    fontWeight = FontWeight.Black,
                    fontSize = 45.sp,
                    lineHeight = 45.sp,
                ),
                displaySmall = TextStyle(
                    fontFamily = Fonts.LCTPicon.Regular,
                    fontWeight = FontWeight.Black,
                    fontSize = 36.sp,
                    lineHeight = 36.sp,
                ),
                headlineLarge = TextStyle(
                    fontFamily = Fonts.LCTPicon.Regular,
                    fontWeight = FontWeight.Black,
                    fontSize = 32.sp,
                    lineHeight = 32.sp,
                ),
                headlineMedium = TextStyle(
                    fontFamily = Fonts.LCTPicon.Regular,
                    fontWeight = FontWeight.Black,
                    fontSize = 28.sp,
                    lineHeight = 28.sp,
                ),
                headlineSmall = TextStyle(
                    fontFamily = Fonts.LCTPicon.Regular,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    lineHeight = 24.sp,
                ),
                titleLarge = TextStyle(
                    fontFamily = Fonts.LCTPicon.Regular,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    lineHeight = 22.sp,
                ),
                titleMedium = TextStyle(
                    fontFamily = Fonts.LCTPicon.Regular,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                ),
                titleSmall = TextStyle(
                    fontFamily = Fonts.LCTPicon.Regular,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                ),
                bodyLarge = TextStyle(
                    fontFamily = Fonts.LCTPicon.Regular,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                ),
                bodyMedium = TextStyle(
                    fontFamily = Fonts.LCTPicon.Regular,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                ),
                bodySmall = TextStyle(
                    fontFamily = Fonts.LCTPicon.Regular,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                ),
                labelLarge = TextStyle(
                    fontFamily = Fonts.LCTPicon.Regular,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                ),
                labelMedium = TextStyle(
                    fontFamily = Fonts.LCTPicon.Regular,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                ),
                labelSmall = TextStyle(
                    fontFamily = Fonts.LCTPicon.Regular,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                ),
            )

    val shapes: Shapes = Shapes(
        extraSmall = RoundedCornerShape(8.dp),
        small = RoundedCornerShape(16.dp),
        medium = RoundedCornerShape(24.dp),
        large = RoundedCornerShape(32.dp),
        extraLarge = RoundedCornerShape(56.dp),
        largeIncreased = RoundedCornerShape(40.dp),
        extraLargeIncreased = RoundedCornerShape(64.dp),
        extraExtraLarge = RoundedCornerShape(96.dp),
    )

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

@Composable
fun KodeinMaterialTheme(
    isDark: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (isDark) KodeinTheme.darkColorScheme else KodeinTheme.lightColorScheme,
        typography = KodeinTheme.typography,
        shapes = KodeinTheme.shapes,
        content = content,
    )
}
