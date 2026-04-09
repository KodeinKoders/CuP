package net.kodein.cup.sa

import androidx.compose.ui.text.TextRange
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import net.kodein.cup.InternalCupAPI
import net.kodein.cup.sa.utils.filterContaining


public typealias SAStep = ImmutableMap<SABlock.ID, ImmutableList<SAData.State>>

@InternalCupAPI
public data class SAData(
    val fullText: String = "",
    val blocks: ImmutableList<SABlock> = persistentListOf(),
    val steps: ImmutableList<SAStep> = persistentListOf(persistentMapOf()),
) {
    @InternalCupAPI
    public sealed class State {
        public data object Hidden : State()
        public data object Highlighted : State()
        public data class Styled(
            val style: SAStyle,
        ) : State()
    }
}

@InternalCupAPI
public fun SAData.hiddenRanges(step: Int): List<TextRange> =
    steps[step]
        .filter { SAData.State.Hidden in it.value }
        .keys
        .map { id -> blocks.first { it.id == id }.range }
        .filterContaining()
