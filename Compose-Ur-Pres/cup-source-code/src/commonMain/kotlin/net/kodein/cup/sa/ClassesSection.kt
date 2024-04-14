package net.kodein.cup.sa

import androidx.compose.ui.text.TextRange
import net.kodein.cup.sa.utils.compareTo


public data class ClassesSection(
    val range: TextRange,
    val classes: List<String>
) : Comparable<ClassesSection> {
    override fun compareTo(other: ClassesSection): Int = range.compareTo(other.range)
}
