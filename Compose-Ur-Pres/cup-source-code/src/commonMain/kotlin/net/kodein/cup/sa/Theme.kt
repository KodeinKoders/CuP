package net.kodein.cup.sa

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import net.kodein.cup.sa.utils.StyleSection


public typealias SourceCodeTheme = (String) -> SpanStyle?

private val emptyStyle = SpanStyle()

public fun List<ClassesSection>.applySourceCodeTheme(theme: SourceCodeTheme): List<StyleSection> =
    mapNotNull { section ->
        var style = emptyStyle
        section.classes.forEach { cls ->
            val classStyle = theme(cls)
            if (classStyle != null) {
                style = style.merge(classStyle)
            }
        }
        if (style === emptyStyle) null
        else StyleSection(section.range, style)
    }

public object SourceCodeThemes {

    public val intelliJLight: SourceCodeTheme = { cls ->
        when (cls) {
            "default",
            -> SpanStyle(
                color = Color(0xFF_000000)
            )

            "subst",
            "title",
            -> SpanStyle(
                color = Color(0xFF_000000),
                fontWeight = FontWeight.Normal,
            )

            "function",
            -> SpanStyle(
                color = Color(0xFF_7A7A43)
            )

            "code",
            "comment",
            "quote",
            -> SpanStyle(
                color = Color(0xFF_8C8C8C),
                fontStyle = FontStyle.Italic,
            )

            "meta",
            -> SpanStyle(
                color = Color(0xFF_9E880D),
            )

            "section",
            "property",
            "attr",
            -> SpanStyle(
                color = Color(0xFF_871094),
            )

            "language",
            "symbol",
            "selector-class",
            "selector-id",
            "selector-tag",
            "selector-attr",
            "selector-pseudo",
            "keyword",
            "literal",
            "name",
            "built_in",
            "type",
            -> SpanStyle(
                color = Color(0xFF_0033B3),
            )

            "attribute",
            -> SpanStyle(
                color = Color(0xFF_174AD4),
            )

            "number",
            -> SpanStyle(
                color = Color(0xFF_1750EB)
            )

            "regexp",
            -> SpanStyle(
                color = Color(0xFF_264EFF)
            )

            "link",
            -> SpanStyle(
                color = Color(0xFF_006DCC),
                textDecoration = TextDecoration.Underline,
            )

            "string",
            -> SpanStyle(
                color = Color(0xFF_067D17),
            )

            "escape",
            -> SpanStyle(
                color = Color(0xFF_0037A6),
            )

            "doctag",
            -> SpanStyle(
                textDecoration = TextDecoration.Underline,
            )

            "template-variable",
            -> SpanStyle(
                color = Color(0xFF_248F8F),
            )

            "addition",
            -> SpanStyle(
                background = Color(0xFF_BEE6BE),
            )

            "deletion",
            -> SpanStyle(
                background = Color(0xFF_D6D6D6),
            )

            "emphasis",
            -> SpanStyle(
                fontStyle = FontStyle.Italic,
            )

            "strong",
            -> SpanStyle(
                fontWeight = FontWeight.Bold,
            )

            else -> null
        }
    }

    public val darcula: SourceCodeTheme = { cls ->
        when (cls) {
            "default",
                -> SpanStyle(
                    color = Color(0xFF_a9b7c6)
                )

            "comment",
                -> SpanStyle(
                    color = Color(0xFF_606366)
                )

            "tag",
                -> SpanStyle(
                    color = Color(0xFF_a4a3a3)
                )

            "",
                -> SpanStyle(
                    color = Color(0xFF_a9b7c6)
                )

            "subst",
            "punctuation",
                -> SpanStyle(
                    color = Color(0xFF_a9b7c6)
                )

            "operator",
                -> SpanStyle(
                    color = Color(0xB2_a9b7c6)
                )

            "bullet",
            "variable",
            "template-variable",
            "selector-tag",
            "name",
            "deletion",
                -> SpanStyle(
                    color = Color(0xFF_4eade5)
                )

            "symbol",
            "number",
            "link",
            "attr",
            "constant",
            "literal",
                -> SpanStyle(
                    color = Color(0xFF_689757)
                )

            "title",
            "class",
                -> SpanStyle(
                    color = Color(0xFF_bbb529)
                )

            "strong",
                -> SpanStyle(
                    color = Color(0xFF_bbb529),
                    fontWeight = FontWeight.Bold
                )

            "code",
            "addition",
            "string",
                -> SpanStyle(
                    color = Color(0xFF_6a8759)
                )

            "built_in",
            "doctag",
            "quote",
            "atrule",
            "regexp",
                -> SpanStyle(
                    color = Color(0xFF_629755)
                )

            "attribute",
            "property",
            "function",
            "section",
                -> SpanStyle(
                    color = Color(0xFF_9876aa)
                )

            "type",
            "template-tag",
            "keyword",
                -> SpanStyle(
                    color = Color(0xFF_cc7832)
                )

            "emphasis",
                -> SpanStyle(
                    color = Color(0xFF_cc7832),
                    fontStyle = FontStyle.Italic
                )

            "meta",
                -> SpanStyle(
                    color = Color(0xFF_808080)
                )

            else -> null
        }
    }
}
