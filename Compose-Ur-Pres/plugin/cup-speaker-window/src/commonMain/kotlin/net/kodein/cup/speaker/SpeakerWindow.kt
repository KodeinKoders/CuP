package net.kodein.cup.speaker

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import net.kodein.cup.config.CupConfigurationBuilder
import net.kodein.cup.config.CupConfigurationDsl
import net.kodein.cup.utils.DataMap
import net.kodein.cup.utils.DataMapElement


public sealed class ASpeakerNotes : DataMapElement<ASpeakerNotes>(Key) {
    internal companion object Key : DataMap.Key<ASpeakerNotes>
}

public class SpeakerNotes(
    public val notes: @Composable ColumnScope.() -> Unit = {}
) : ASpeakerNotes()

public class SpeakerNotesMD(
    public val notes: String
) : ASpeakerNotes()

@CupConfigurationDsl
public expect fun CupConfigurationBuilder.speakerWindow()
