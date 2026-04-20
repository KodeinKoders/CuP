package net.kodein.cup

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import net.kodein.cup.Slides.Position
import net.kodein.cup.utils.EagerProperty
import net.kodein.cup.utils.EmptySlideContext
import net.kodein.cup.utils.SlideContext
import net.kodein.cup.utils.eagerProperty
import net.kodein.cup.utils.plus


public sealed interface SlideGroup {
    public val slideList: List<Slide>
}

public class Slides(
    content: List<SlideGroup>,
    private val context: ((Position) -> SlideContext)? = null,
    private val specs: ((Position) -> SlideSpecs)? = null
): SlideGroup {

    public data class Position(
        val indexInGroup: Int,
        val lastGroupIndex: Int,
    )

    public constructor(
        vararg content: SlideGroup,
        context: ((Position) -> SlideContext)? = null,
        specs: ((Position) -> SlideSpecs)? = null
    ) : this(content.toList(), context, specs)

    override val slideList: ImmutableList<Slide> = run {
        val slides = content.flatMap { it.slideList }
        if (specs == null && context == null) slides.toImmutableList()
        else slides.mapIndexed { index, slide ->
            val position = Position(index, slides.lastIndex)
            val mergedSpecs = when {
                slide.specs != null && specs != null -> slide.specs.merge(specs.invoke(position))
                slide.specs == null && specs != null -> specs.invoke(position)
                else -> slide.specs
            }
            val mergedContext = when {
                context != null -> slide.context + context.invoke(position)
                else -> slide.context
            }
            slide.copy(specs = mergedSpecs, context = mergedContext)
        }.toImmutableList()
    }
}

public val Position.isFirst: Boolean get() = indexInGroup == 0
public val Position.isLast: Boolean get() = indexInGroup == lastGroupIndex

public typealias SlideContent = @Composable ColumnScope.(Int) -> Unit

@ExposedCopyVisibility
public data class Slide internal constructor(
    public val name: String,
    public val stepCount: Int = 1,
    public val specs: SlideSpecs? = null,
    public val context: SlideContext = EmptySlideContext,
    public val contentBuilder: @Composable () -> SlideContent
) : SlideGroup {
    public val lastStep: Int get() = stepCount - 1
    override val slideList: List<Slide> get() = listOf(this)
    @PluginCupAPI
    public interface CacheKey
    @PluginCupAPI
    public var cache: HashMap<CacheKey, Any> = HashMap()

    @Deprecated("Renamed to user", ReplaceWith("context"), DeprecationLevel.ERROR)
    public val user: SlideContext = context
}

public fun Slide(
    stepCount: Int = 1,
    specs: SlideSpecs? = null,
    context: SlideContext = EmptySlideContext,
    content: SlideContent
): EagerProperty<Slide> =
    eagerProperty { property ->
        Slide(
            name = property.name,
            stepCount = stepCount,
            specs = specs,
            context = context,
            contentBuilder = { content }
        )
    }

public fun Slide(
    name: String,
    stepCount: Int = 1,
    specs: SlideSpecs? = null,
    context: SlideContext = EmptySlideContext,
    content: SlideContent
): Slide =
    Slide(
        name = name,
        stepCount = stepCount,
        specs = specs,
        context = context,
        contentBuilder = { content }
    )

public object PreparedSlideScope {
    public fun slideContent(content: SlideContent): SlideContent = content
}

@Suppress("FunctionName")
public fun PreparedSlide(
    stepCount: Int = 1,
    specs: SlideSpecs? = null,
    context: SlideContext = EmptySlideContext,
    prepare: @Composable PreparedSlideScope.() -> SlideContent
) : EagerProperty<Slide> =
    eagerProperty { property ->
        Slide(
            name = property.name,
            stepCount = stepCount,
            specs = specs,
            context = context,
            contentBuilder = { PreparedSlideScope.prepare() }
        )
    }

public val LocalSlide: ProvidableCompositionLocal<Slide?> = compositionLocalOf { null }
