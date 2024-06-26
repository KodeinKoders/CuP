package net.kodein.cup.speaker

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import net.kodein.cup.config.CupConfigurationBuilder
import net.kodein.cup.config.CupConfigurationDsl
import net.kodein.cup.utils.DataMap
import net.kodein.cup.utils.DataMapElement


@Deprecated("Speaker notes are now forcibly in Markdown", level = DeprecationLevel.ERROR)
@Suppress("DEPRECATION_ERROR")
public sealed class ASpeakerNotes : DataMapElement<ASpeakerNotes>(Key) {
    internal companion object Key : DataMap.Key<ASpeakerNotes>
}

public class SpeakerNotes(
    public val md: String
) : DataMapElement<SpeakerNotes>(Key) {
    internal companion object Key : DataMap.Key<SpeakerNotes>

    @Deprecated("Speaker notes are now forcibly in Markdown, please use SpeakerNotes(markdownText)")
    public constructor(notes: @Composable ColumnScope.() -> Unit = {}) : this("") {
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
