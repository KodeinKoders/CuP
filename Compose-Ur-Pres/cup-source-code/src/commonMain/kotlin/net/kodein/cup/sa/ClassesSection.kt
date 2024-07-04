package net.kodein.cup.sa

import androidx.compose.ui.text.TextRange
import kotlinx.collections.immutable.ImmutableList
import net.kodein.cup.PluginCupAPI
import net.kodein.cup.sa.utils.compareTo


@PluginCupAPI
public data class ClassesSection(
    val range: TextRange,
    val classes: ImmutableList<String>
) : Comparable<ClassesSection> {
    override fun compareTo(other: ClassesSection): Int = range.compareTo(other.range)
}
