package net.kodein.cup.imgexp

import net.kodein.cup.config.CupConfigurationBuilder
import net.kodein.cup.utils.DataMap
import net.kodein.cup.utils.DataMapElement


public class Export private constructor(
    public val steps: List<Int>,
    internal val type: Type
) : DataMapElement<Export>(Key) {
    internal enum class Type { Only, Ignore }
    public companion object Key : DataMap.Key<Export> {
        public fun only(vararg steps: Int): Export = Export(steps.asList(), Type.Only)
        public fun ignore(vararg steps: Int): Export = Export(steps.asList(), Type.Ignore)
        public fun none(): Export = Export(emptyList(), Type.Only)
    }
}

public expect fun CupConfigurationBuilder.imageExport()
