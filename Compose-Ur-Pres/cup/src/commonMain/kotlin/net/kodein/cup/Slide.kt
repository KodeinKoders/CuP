package net.kodein.cup

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import net.kodein.cup.utils.*


public interface SlideGroup {
    public val slideList: List<Slide>
}

private val defaultSpecsBuilder: SlideSpecBuilder = { this }

public class Slides(
    private val content: List<SlideGroup>,
    private val user: DataMap = emptyDataMap(),
    private val specs: SlideSpecBuilder = defaultSpecsBuilder
): SlideGroup {

    public constructor(
        vararg content: SlideGroup,
        user: DataMap = emptyDataMap(),
        specs: @Composable SlideSpecs.(Slide.Configuration) -> SlideSpecs = defaultSpecsBuilder
    ) : this(content.toList(), user, specs)

    override val slideList: List<Slide> by lazy {
        val slides = content.flatMap { it.slideList }
        if (specs === defaultSpecsBuilder && user.isEmpty()) slides
        else slides.mapIndexed { index, slide ->
            val wrappedSpecs: SlideSpecBuilder =
                if (specs !== defaultSpecsBuilder) ({ originalConfig ->
                    val wrappedConfig = Slide.Configuration(
                        indexInGroup = index,
                        lastGroupIndex = slides.lastIndex
                    )
                    slide.specs.invoke(specs(this, wrappedConfig), originalConfig)
                })
                else slide.specs
            val wrappedUser: DataMap =
                if (user.isNotEmpty()) user + slide.user
                else slide.user
            slide.copy(specs = wrappedSpecs, user = wrappedUser)
        }
    }
}

public typealias SlideContent = @Composable ColumnScope.(Int) -> Unit
public typealias SlideSpecBuilder = @Composable SlideSpecs.(Slide.Configuration) -> SlideSpecs

public data class Slide internal constructor(
    public val name: String,
    public val stepCount: Int = 1,
    public val specs: SlideSpecBuilder = defaultSpecsBuilder,
    public val user: DataMap = emptyDataMap(),
    public val content: @Composable () -> SlideContent
) : SlideGroup {
    public data class Configuration(
        val indexInGroup: Int,
        val lastGroupIndex: Int,
    )

    public val lastStep: Int get() = stepCount - 1
    override val slideList: List<Slide> get() = listOf(this)
}

public fun Slide(
    stepCount: Int = 1,
    specs: SlideSpecBuilder = defaultSpecsBuilder,
    user: DataMap = emptyDataMap(),
    content: SlideContent
): EagerProperty<Slide> =
    eagerProperty { property ->
        Slide(
            name = property.name,
            stepCount = stepCount,
            specs = specs,
            user = user,
            content = { content }
        )
    }

public object PreparedSlideScope {
    public fun slideContent(content: SlideContent): SlideContent = content
}

@Suppress("FunctionName")
public fun PreparedSlide(
    stepCount: Int = 1,
    specs: SlideSpecBuilder = defaultSpecsBuilder,
    user: DataMap = emptyDataMap(),
    prepare: @Composable PreparedSlideScope.() -> SlideContent
) : EagerProperty<Slide> =
    eagerProperty { property ->
        Slide(
            name = property.name,
            stepCount = stepCount,
            specs = specs,
            user = user,
            content = { PreparedSlideScope.prepare() }
        )
    }
