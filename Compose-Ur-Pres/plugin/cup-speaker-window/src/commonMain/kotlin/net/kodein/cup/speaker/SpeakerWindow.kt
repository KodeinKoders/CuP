package net.kodein.cup.speaker

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import net.kodein.cup.config.CupConfigurationBuilder
import net.kodein.cup.config.CupConfigurationDsl
import net.kodein.cup.utils.DataMap
import net.kodein.cup.utils.DataMapElement


@Deprecated("Speaker notes are now forcibly in Markdown", level = DeprecationLevel.ERROR)
@Suppress("DEPRECATION_ERROR")
public sealed class ASpeakerNotes : DataMapElement<ASpeakerNotes>(Key) {
    internal companion object Key : DataMap.Key<ASpeakerNotes>
}

@Stable
public class SpeakerNotes(
    notes: List<Pair<IntRange, String>>
) : DataMapElement<SpeakerNotes>(Key) {
    internal companion object Key : DataMap.Key<SpeakerNotes> {
        private val allSteps = 0..Int.MAX_VALUE
    }

    public val notes: ImmutableList<Pair<IntRange, String>> = notes.toImmutableList()

    public constructor(md: String): this(listOf(allSteps to md))

    @Deprecated("Speaker notes are now forcibly in Markdown, please use SpeakerNotes(markdownText)")
    public constructor(notes: @Composable ColumnScope.() -> Unit = {}) : this(emptyList()) {
        error("Speaker notes are now forcibly in Markdown, please use SpeakerNotes(markdownText)")
    }
}

@Deprecated(
    message = "Speaker notes are now forcibly in Markdown",
    level = DeprecationLevel.ERROR,
    replaceWith = ReplaceWith("SpeakerNotes(notes)")
)
@Suppress("DEPRECATION_ERROR")
public class SpeakerNotesMD(
    public val notes: String
) : ASpeakerNotes()

@CupConfigurationDsl
public expect fun CupConfigurationBuilder.speakerWindow()

@Composable
public expect fun isInSpeakerWindow(): Boolean
