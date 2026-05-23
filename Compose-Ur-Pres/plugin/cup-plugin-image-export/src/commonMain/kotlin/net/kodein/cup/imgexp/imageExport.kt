package net.kodein.cup.imgexp

import net.kodein.cup.Slide
import net.kodein.cup.config.CupConfigurationBuilder
import net.kodein.cup.utils.SlideContext
import net.kodein.cup.utils.SlideContextElement


public class Export private constructor(
    public val steps: List<Int>,
    internal val type: Type
) : SlideContextElement<Export>(Key) {
    internal enum class Type { Only, Ignore }
    public companion object Key : SlideContext.Key<Export> {
        public fun only(vararg steps: Int): Export = Export(steps.asList(), Type.Only)
        public fun only(vararg steps: IntRange): Export = Export(steps.flatMap { it }, Type.Only)
        public fun ignore(vararg steps: Int): Export = Export(steps.asList(), Type.Ignore)
        public fun ignore(vararg steps: IntRange): Export = Export(steps.flatMap { it }, Type.Ignore)
        public fun none(): Export = Export(emptyList(), Type.Only)
    }

    override fun toString(): String = "Export(type=$type, steps=$steps)"
}

public expect fun CupConfigurationBuilder.imageExport()

internal fun Slide.getExportedSteps(): Iterable<Int> {
    val slideExport = context[Export.Key]
    return when (slideExport?.type) {
        Export.Type.Only -> (0..<stepCount).filter { it in slideExport.steps }
        Export.Type.Ignore -> (0..<stepCount).filterNot { it in slideExport.steps }
        null -> (0..<stepCount)
    }
}