package net.kodein.cup

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import net.kodein.cup.utils.*


public sealed interface SlideGroup {
    public val slideList: List<Slide>
}

public class Slides(
    content: List<SlideGroup>,
    private val user: ((Position) -> DataMap)? = null,
    private val specs: ((Position) -> SlideSpecs)? = null
): SlideGroup {

    public data class Position(
        val indexInGroup: Int,
        val lastGroupIndex: Int,
    )

    public constructor(
        vararg content: SlideGroup,
        user: ((Position) -> DataMap)? = null,
        specs: ((Position) -> SlideSpecs)? = null
    ) : this(content.toList(), user, specs)

    override val slideList: ImmutableList<Slide> = run {
        val slides = content.flatMap { it.slideList }
        if (specs == null && user == null) slides.toImmutableList()
        else slides.mapIndexed { index, slide ->
            val position = Position(index, slides.lastIndex)
            val mergedSpecs = when {
                slide.specs != null && specs != null -> slide.specs.merge(specs.invoke(position))
                slide.specs == null && specs != null -> specs.invoke(position)
                else -> slide.specs
            }
            val mergedUser = when {
                user != null -> slide.user + user.invoke(position)
                else -> slide.user
            }
            slide.copy(specs = mergedSpecs, user = mergedUser)
        }.toImmutableList()
    }
}

public val Slides.Position.isFirst: Boolean get() = indexInGroup == 0
public val Slides.Position.isLast: Boolean get() = indexInGroup == lastGroupIndex

public typealias SlideContent = @Composable ColumnScope.(Int) -> Unit

@ExposedCopyVisibility
public data class Slide internal constructor(
    public val name: String,
    public val stepCount: Int = 1,
    public val specs: SlideSpecs? = null,
    public val user: DataMap = emptyDataMap(),
    public val contentBuilder: @Composable () -> SlideContent
) : SlideGroup {
    public val lastStep: Int get() = stepCount - 1
    override val slideList: List<Slide> get() = listOf(this)
}

public fun Slide(
    stepCount: Int = 1,
    specs: SlideSpecs? = null,
    user: DataMap = emptyDataMap(),
    content: SlideContent
): EagerProperty<Slide> =
    eagerProperty { property ->
        Slide(
            name = property.name,
            stepCount = stepCount,
            specs = specs,
            user = user,
            contentBuilder = { content }
        )
    }

public fun Slide(
    name: String,
    stepCount: Int = 1,
    specs: SlideSpecs? = null,
    user: DataMap = emptyDataMap(),
    content: SlideContent
): Slide =
    Slide(
        name = name,
        stepCount = stepCount,
        specs = specs,
        user = user,
        contentBuilder = { content }
    )

public object PreparedSlideScope {
    public fun slideContent(content: SlideContent): SlideContent = content
}

@Suppress("FunctionName")
public fun PreparedSlide(
    stepCount: Int = 1,
    specs: SlideSpecs? = null,
    user: DataMap = emptyDataMap(),
    prepare: @Composable PreparedSlideScope.() -> SlideContent
) : EagerProperty<Slide> =
    eagerProperty { property ->
        Slide(
            name = property.name,
            stepCount = stepCount,
            specs = specs,
            user = user,
            contentBuilder = { PreparedSlideScope.prepare() }
        )
    }
