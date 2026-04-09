package net.kodein.cup.sa.utils

import androidx.compose.ui.text.TextRange
import net.kodein.cup.sa.ClassesSection
import kotlin.collections.ArrayList


public fun TextRange.positive(): TextRange =
    if (start > end) TextRange(min, max)
    else this

public operator fun TextRange.compareTo(other: TextRange): Int =
    when {
        this.min != other.min -> this.min - other.min
        this.max != other.max -> other.max - this.max
        else -> 0
    }

public fun Iterable<TextRange>.filterContaining(): List<TextRange> {
    val containingRanges = ArrayList<TextRange>()
    this.sortedWith(TextRange::compareTo).forEach { range ->
        if (containingRanges.none { it.contains(range) }) {
            containingRanges.add(range)
        }
    }
    return containingRanges
}

public operator fun String.minus(ranges: Iterable<TextRange>): String {
    val fullText = this
    return buildString {
        var lastEnd = 0
        ranges.filterContaining().forEach {
            if (lastEnd != it.min) {
                append(fullText.substring(lastEnd, it.min))
            }
            lastEnd = it.max
        }
        if (lastEnd < fullText.length) {
            append(fullText.substring(lastEnd))
        }
    }
}

public fun <Input, Node> List<Input>.asTree(
    range: Input.() -> TextRange,
    node: (Input, List<Node>) -> Node
): List<Node> {
    if (this.isEmpty()) return emptyList()

    val leaves = ArrayList(this.sortedWith { l, r -> l.range().compareTo(r.range()) })

    fun buildNode(parent: Input): Node {
        val children = ArrayList<Node>()
        while (leaves.isNotEmpty() && parent.range().contains(leaves.first().range())) {
            val child = leaves.removeFirst()
            children.add(buildNode(child))
        }
        return node(parent, children)
    }

    val list = ArrayList<Node>()
    while (leaves.isNotEmpty()) {
        val root = leaves.removeFirst()
        list.add(buildNode(root))
    }

    return list
}

public fun String.lineRanges(): List<TextRange> = buildList {
    var start = 0
    while (true) {
        val nl = this@lineRanges.indexOf('\n', start)
        if (nl == -1) break
        add(TextRange(start, nl + 1))
        start = nl + 1
    }
    if (start != this@lineRanges.length) {
        add(TextRange(start, this@lineRanges.length))
    }
}

public fun List<ClassesSection>.offset(hiddenRanges: Iterable<TextRange>): List<ClassesSection> =
    map { section ->
        var start = section.range.start
        hiddenRanges.forEach { if (it.start <= start) start += it.length }
        var end = section.range.end
        hiddenRanges.forEach { if (it.start < end) end += it.length }
        section.copy(range = TextRange(start, end))
    }
