package net.kodein.cup.sa.utils

import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange


public data class StyleSection(
    public val range: TextRange,
    public val style: SpanStyle
)

public typealias StyleApplier = AnnotatedString.Builder.(List<StyleSection>) -> Unit
public typealias DrawApplier = ContentDrawScope.(TextLayoutResult) -> Unit

public fun AnnotatedString.Builder.addStyles(sections: Iterable<StyleSection>) {
    sections.forEach {
        addStyle(it.style, it.range.min, it.range.max)
    }
}

public fun Iterable<StyleSection>.restrict(inRanges: Iterable<TextRange>): List<StyleSection> =
    this
        .filterNot { it.range.collapsed }
        .flatMap { section ->
            inRanges.mapNotNull { inRange ->
/*    │   │    */   when {
/* ╠═╣│   │    */       section.range.max <= inRange.min -> null
/*   ╠╪╣  │    */       section.range.min <= inRange.min && section.range.max < inRange.max -> StyleSection(TextRange(inRange.min, section.range.max) , section.style)
/*   ╠╪═══╪╣   */       section.range.min <= inRange.min && section.range.max >= inRange.max -> StyleSection(TextRange(inRange.min, inRange.max) , section.style)
/*    │╠═╣│    */       section.range.min > inRange.min && section.range.max < inRange.max -> StyleSection(TextRange(section.range.min, section.range.max) , section.style)
/*    │  ╠╪╣   */       section.range.min < inRange.max && section.range.max >= inRange.max -> StyleSection(TextRange(section.range.min, inRange.max) , section.style)
/*    │   │╠═╣ */       section.range.min >= inRange.max -> null
/*    │   │    */       else -> error("Impossible section ${section.range} applied in range $inRange")
                    }
            }
        }

public fun Iterable<StyleSection>.offset(toRange: TextRange): List<StyleSection> =
    this
        .filterNot { it.range.collapsed }
        .mapNotNull { section ->
/*    │   │    */   when {
/* ╠═╣│   │    */       section.range.max <= toRange.min -> null
/*   ╠╪╣  │    */       section.range.min <= toRange.min && section.range.max < toRange.max -> section.copy(range = TextRange(0, section.range.max - toRange.min))
/*   ╠╪═══╪╣   */       section.range.min <= toRange.min && section.range.max >= toRange.max -> section.copy(range = TextRange(0, toRange.length))
/*    │╠═╣│    */       section.range.min > toRange.min && section.range.max < toRange.max -> section.copy(range = TextRange(section.range.min - toRange.min, section.range.max - toRange.min))
/*    │  ╠╪╣   */       section.range.min < toRange.max && section.range.max >= toRange.max -> section.copy(range = TextRange(section.range.min - toRange.min, toRange.length))
/*    │   │╠═╣ */       section.range.min >= toRange.max -> null
/*    │   │    */       else -> error("Impossible section ${section.range} offset to range $toRange")
                    }
    }

public fun Iterable<StyleSection>.merge(other: StyleSection): List<StyleSection> =
    this
        .filterNot { it.range.collapsed }
        .flatMap { section ->
/*    │   │    */   when {
/* ╠═╣│   │    */       section.range.max <= other.range.min -> listOf(section)
/*   ╠╪╣  │    */       section.range.min <= other.range.min && section.range.max <= other.range.max -> listOf(
/*    │   │    */           StyleSection(TextRange(section.range.min, other.range.min), section.style),
/*    │   │    */           StyleSection(TextRange(other.range.min, section.range.max), section.style.merge(other.style)),
/*    │   │    */       )
/*   ╠╪═══╪╣   */       section.range.min <= other.range.min && section.range.max > other.range.max -> listOf(
/*    │   │    */           StyleSection(TextRange(section.range.min, other.range.min), section.style),
/*    │   │    */           StyleSection(other.range, section.style.merge(other.style)),
/*    │   │    */           StyleSection(TextRange(other.range.max, section.range.max), section.style),
/*    │   │    */       )
/*    │╠═╣│    */       section.range.min > other.range.min && section.range.max <= other.range.max -> listOf(section.copy(style = section.style.merge(other.style)))
/*    │  ╠╪╣   */       section.range.min <= other.range.max && section.range.max > other.range.max -> listOf(
/*    │   │    */           StyleSection(TextRange(section.range.min, other.range.max), section.style.merge(other.style)),
/*    │   │    */           StyleSection(TextRange(other.range.max, section.range.max), section.style),
/*    │   │    */       )
/*    │   │╠═╣ */       section.range.min > other.range.max -> listOf(section)
/*    │   │    */       else -> error("Impossible section ${section.range} overridden by range ${other.range}")
                    }
        } + other

public fun Iterable<StyleSection>.merge(others: Iterable<StyleSection>): List<StyleSection> {
    var result = toList()
    others.forEach { result = result.merge(it) }
    return result
}
