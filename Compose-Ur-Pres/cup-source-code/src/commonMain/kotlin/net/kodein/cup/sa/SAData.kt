package net.kodein.cup.sa

import androidx.compose.ui.text.TextRange
import net.kodein.cup.InternalCupAPI
import net.kodein.cup.sa.utils.filterContaining


public typealias SAStep = Map<SABlock.ID, List<SAData.State>>

@InternalCupAPI
public data class SAData(
    val fullText: String = "",
    val blocks: List<SABlock> = emptyList(),
    val steps: List<SAStep> = listOf(emptyMap()),
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
