package net.kodein.cup.ui

import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import net.kodein.cup.utils.EagerProperty
import net.kodein.cup.utils.eagerProperty


public abstract class SpanStyleSheet {
    public class Marker internal constructor(
        public val id: String,
        public val style: SpanStyle
    ) {
        init {
            require(id.matches(Regex("[a-zA-Z0-9_-]+"))) { "Bad ID (must match regex \"[a-zA-Z0-9_-]+\")" }
        }

        override fun toString(): String = "\${$id}"
    }

    internal val markers = HashMap<String, Marker>()

    public fun registerMarker(style: SpanStyle): EagerProperty<Marker> =
        eagerProperty { prop ->
            Marker(
                id = prop.name,
                style = style
            ).also { markers[prop.name] = it }
        }

    public operator fun Marker.unaryPlus(): String = "$OPEN+$id$CLOSE"
    public operator fun Marker.unaryMinus(): String = "$OPEN-$id$CLOSE"

    /** Bold */
    public val b: Marker by registerMarker(SpanStyle(fontWeight = FontWeight.Bold))
    /** Italic */
    public val i: Marker by registerMarker(SpanStyle(fontStyle = FontStyle.Italic))

    public fun IC(id: String): String {
        require(id.matches(Regex("[a-zA-Z0-9_-]+"))) { "Bad ID (must match regex \"[a-zA-Z0-9_-]+\")" }
        return "$OPEN!$id$CLOSE"
    }

    public companion object : SpanStyleSheet() {
        internal const val OPEN  = "\u2062«\u2064"
        internal const val CLOSE = "\u2064»\u2062"
    }
}

private val markerRegex = Regex("${SpanStyleSheet.OPEN}(?<type>[!+-])(?<id>[a-zA-Z0-9_-]+)${SpanStyleSheet.CLOSE}")

public fun <S : SpanStyleSheet> styled(styleSheet: S, build: S.() -> String): AnnotatedString {
    val str = styleSheet.build()

    val startMarkers = ArrayList<Pair<Int, SpanStyleSheet.Marker>>()
    val endMarkers = ArrayList<Pair<Int, SpanStyleSheet.Marker>>()
    val annotatedString = AnnotatedString.Builder()
    var start = 0
    markerRegex.findAll(str).forEach { result ->
        annotatedString.append(str, start, result.range.first)
        start = result.range.last + 1
        val type = result.groups["type"]!!.value
        val id = result.groups["id"]!!.value
        when (type) {
            "+" -> startMarkers.add(Pair(annotatedString.length, styleSheet.markers[id] ?: error("Unknown marker \"$id\".")))
            "-" -> endMarkers.add(Pair(annotatedString.length, styleSheet.markers[id] ?: error("Unknown marker \"$id\".")))
            "!" -> annotatedString.appendInlineContent(id)
        }
    }
    annotatedString.append(str, start, str.length)
    startMarkers.forEach { (startIndex, startMarker) ->
        val endMarkerIndex = endMarkers.indexOfFirst { (endIndex, endMarker) -> endIndex > startIndex && endMarker == startMarker }
        val endIndex = if (endMarkerIndex >= 0) {
            endMarkers.removeAt(endMarkerIndex).first
        } else {
            annotatedString.length
        }
        annotatedString.addStyle(startMarker.style, startIndex, endIndex)
    }
    return annotatedString.toAnnotatedString()
}

public inline fun styled(crossinline build: SpanStyleSheet.() -> String): AnnotatedString = styled(SpanStyleSheet) { build() }
