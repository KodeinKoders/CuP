package net.kodein.cup.sa

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.substring
import net.kodein.cup.sa.utils.compareTo
import kotlin.jvm.JvmInline


public data class SABlock(
    val id: ID,
    val type: Type,
    val range: TextRange,
    val debugName: String,
) : Comparable<SABlock> {
    override fun compareTo(other: SABlock): Int = range.compareTo(other.range)

    public enum class Type { Lines, Inline }

    @JvmInline
    public value class ID(public val id: Int) {
        public companion object {
            public val None: ID = ID(-1)
        }
    }
}

public fun List<SABlock>.getById(id: SABlock.ID): SABlock = first { it.id == id }

public fun SABlock.isValid(text: String): Boolean = when (type) {
    SABlock.Type.Lines -> (range.min == 0 || text[range.min - 1] == '\n') && (range.max >= text.length || text[range.max - 1] == '\n')
    SABlock.Type.Inline -> "\n" !in text.substring(range)
}

public fun SABlock(
    id: SABlock.ID,
    text: String,
    selection: TextRange,
    debugName: String,
): SABlock? {
    if (selection.collapsed) return null

    val selected = text.substring(selection)

    val startOfLine = selection.min == 0 || text[selection.min - 1] == '\n'
    val endOfLine = selection.max == text.length || text[selection.max] == '\n' || text[selection.max - 1] == '\n'
    val isLine = startOfLine && endOfLine

    if ('\n' !in selected && !isLine) {
        return SABlock(id, SABlock.Type.Inline, selection, debugName)
    }

    if (isLine) {
        val end = if (selection.max == text.length || text[selection.max - 1] == '\n') selection.max else selection.max + 1
        return SABlock(id, SABlock.Type.Lines, TextRange(selection.min, end), debugName)
    }

    return null
}