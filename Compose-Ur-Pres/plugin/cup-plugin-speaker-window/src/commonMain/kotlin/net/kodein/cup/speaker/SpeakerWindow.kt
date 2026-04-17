package net.kodein.cup.speaker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import net.kodein.cup.config.CupConfigurationBuilder
import net.kodein.cup.utils.DataMap
import net.kodein.cup.utils.DataMapElement


@Stable
public class SpeakerNotes(
    notes: List<Pair<IntRange, String>>
) : DataMapElement<SpeakerNotes>(Key) {
    internal companion object Key : DataMap.Key<SpeakerNotes> {
        private val allSteps = 0..Int.MAX_VALUE
    }

    public val notes: ImmutableList<Pair<IntRange, String>> = notes.toImmutableList()

    public constructor(md: String): this(listOf(allSteps to md))
}

public expect fun CupConfigurationBuilder.speakerWindow()

@Composable
public expect fun isInSpeakerWindow(): Boolean
