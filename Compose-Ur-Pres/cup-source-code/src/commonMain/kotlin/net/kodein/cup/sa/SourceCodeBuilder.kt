package net.kodein.cup.sa

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.TextRange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import net.kodein.cup.sa.utils.minus
import net.kodein.cup.sa.utils.offset
import net.kodein.cup.utils.EagerProperty
import net.kodein.cup.utils.eagerProperty
import kotlin.math.max


public class SourceCodeBuilder internal constructor() {

    private var markerCounter = 0
    internal val markers = ArrayList<Marker>()
    internal var lastEmptyStep: Int = 0

    public data class Marker internal constructor(
        internal val id: Int,
        val name: String,
        val visibilities: List<State>
    ) {
        override fun toString(): String = "$START_OPEN$id$START_CLOSE"
    }

    @Suppress("PropertyName")
    public val X: String get() = END

    public fun marker(vararg visibilities: State): EagerProperty<Marker> =
        eagerProperty { prop ->
            Marker(
                id = markerCounter++,
                name = prop.name,
                visibilities = visibilities.toList()
            ).also { markers += it }
        }
    public fun emptyStep(step: Int) {
        lastEmptyStep = max(step, lastEmptyStep)
    }

    internal companion object {
        const val START_OPEN  = "\u2062«\u2064"
        const val START_CLOSE = "\u2064:\u2062"
        const val END = "\u2063»\u2063"

        val openRegex = Regex("$START_OPEN([0-9]+)$START_CLOSE")
    }

    public sealed interface State { public val steps: List<IntRange> }
    internal data class Hidden(override val steps: List<IntRange>) : State
    internal data class OnlyShown(override val steps: List<IntRange>) : State
    internal data class Highlighted(override val steps: List<IntRange>) : State
    internal data class Styled(override val steps: List<IntRange>, val style: SAStyle) : State

    public fun hidden(vararg steps: Int): State = Hidden(steps.map { IntRange(it, it) })
    public fun hidden(vararg steps: IntRange): State = Hidden(steps.asList())
    public fun onlyShown(vararg steps: Int): State = OnlyShown(steps.map { IntRange(it, it) })
    public fun onlyShown(vararg steps: IntRange): State = OnlyShown(steps.asList())
    public fun highlighted(vararg steps: Int): State = Highlighted(steps.map { IntRange(it, it) })
    public fun highlighted(vararg steps: IntRange): State = Highlighted(steps.asList())
    public fun styled(style: SAStyle, vararg steps: Int): State = Styled(steps.map { IntRange(it, it) }, style)
    public fun styled(style: SAStyle, vararg steps: IntRange): State = Styled(steps.asList(), style)
}

private val sourceHighlighter by lazy { SourceHighlighter() }
public suspend fun initSourceCodeHighlighting() { sourceHighlighter.joinInit() }

@Suppress("ReplaceRangeStartEndInclusiveWithFirstLast")
private fun prepareSourceCode(
    language: String,
    create: SourceCodeBuilder.() -> String,
    scope: CoroutineScope
): SourceCode {
    val builder = SourceCodeBuilder()
    val text = create(builder)
    val markers = builder.markers

    var cleanText = text
    val stack = ArrayList<Pair<SourceCodeBuilder.Marker, Int>>()
    val ranges = ArrayList<Pair<SourceCodeBuilder.Marker, TextRange>>()

    while (true) {
        val startMatch = SourceCodeBuilder.openRegex.find(cleanText)
        val endPos = cleanText.indexOf(SourceCodeBuilder.END)

        when {
            startMatch != null && (endPos == -1 || startMatch.range.start < endPos) -> {
                cleanText = cleanText.replaceRange(startMatch.range, "")
                val id = startMatch.groupValues[1].toInt()
                val marker = markers.firstOrNull { it.id == id } ?: error("Use of a marker that is not created on this InlineSourceCode")
                stack.add(marker to startMatch.range.start)
                continue
            }
            endPos >= 0 && (startMatch == null || endPos < startMatch.range.start) -> {
                cleanText = cleanText.replaceRange(endPos..<(endPos + SourceCodeBuilder.END.length), "")
                require(stack.isNotEmpty()) { "At $endPos: Empty stack: close marker does not corresponds to any opened segment." }
                val (marker, startPos) = stack.removeLast()
                ranges.add(marker to TextRange(startPos, endPos))
                continue
            }
            else -> break
        }
    }

    var nextBlockId = SABlock.ID(0)
    val correspondence = HashMap<Int, ArrayList<SABlock.ID>>()
    val blocks = ranges.map { (marker, range) ->
        val block = SABlock(nextBlockId, cleanText, range)
        nextBlockId = SABlock.ID(nextBlockId.id + 1)
        check(block != null) { "Invalid block ${marker.name}: a block must either be inside a line (it must not contain new line) or it must contain the entirety of one or multiple line(s)." }
        correspondence.getOrPut(marker.id) { ArrayList() }.add(block.id)
        block
    }

    val max = max(
        markers.flatMap { it.visibilities }.flatMap { it.steps }.maxOfOrNull { it.last } ?: 1,
        builder.lastEmptyStep + 1
    )

    val steps = (0..max).map { stepNumber ->
        val map = HashMap<SABlock.ID, ArrayList<SAData.State>>()
        markers.forEach { marker ->
            marker.visibilities.forEach { markerState ->
                val saState = when {
                    markerState is SourceCodeBuilder.Hidden && markerState.steps.any { stepNumber in it } -> SAData.State.Hidden
                    markerState is SourceCodeBuilder.OnlyShown && markerState.steps.none { stepNumber in it } -> SAData.State.Hidden
                    markerState is SourceCodeBuilder.Highlighted && markerState.steps.any { stepNumber in it } -> SAData.State.Highlighted
                    markerState is SourceCodeBuilder.Styled && markerState.steps.any { stepNumber in it } -> {
                        SAData.State.Styled(markerState.style)
                    }
                    else -> null
                }
                if (saState != null) {
                    correspondence[marker.id]?.forEach { blockId ->
                        map.getOrPut(blockId) { ArrayList() }.add(saState)
                    }
                }
            }
        }
        map
    }

    val data = SAData(
        fullText = cleanText,
        blocks = blocks,
        steps = steps,
    )

    val sections = data.steps.indices.map { stepNumber ->
        val hiddenRanges = data.hiddenRanges(stepNumber)
        val code = data.fullText - hiddenRanges
        scope.async { sourceHighlighter.parse(code, language).offset(hiddenRanges) }
    }

    return SourceCode(
        data = data,
        sections = { step -> sections[step].await() }
    )
}

@Composable
public fun rememberSourceCode(
    language: String,
    key: Any? = null,
    create: SourceCodeBuilder.() -> String,
): SourceCode {
    val scope = rememberCoroutineScope()
    return remember(key) {
        prepareSourceCode(language, create, scope)
    }
}
