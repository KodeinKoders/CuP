package net.kodein.cup

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.LayoutDirection
import kotlinx.coroutines.CoroutineScope
import net.kodein.cup.utils.*
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty


public interface SlideGroup {
    public val slideList: List<Slide>
}

private val defaultSpecs: SlideSpecs.(Slide.Configuration) -> SlideSpecs = { this }

public class Slides(
    private val content: List<SlideGroup>,
    private val user: DataMap = emptyDataMap(),
    private val specs: SlideSpecs.(Slide.Configuration) -> SlideSpecs = defaultSpecs
): SlideGroup {

    public constructor(
        vararg content: SlideGroup,
        user: DataMap = emptyDataMap(),
        specs: SlideSpecs.(Slide.Configuration) -> SlideSpecs = defaultSpecs
    ) : this(content.toList(), user, specs)

    override val slideList: List<Slide> by lazy {
        val slides = content.flatMap { it.slideList }
        if (specs === defaultSpecs && user.isEmpty()) slides
        else slides.mapIndexed { index, slide ->
            val wrappedSpecs: SlideSpecs.(Slide.Configuration) -> SlideSpecs =
                if (specs !== defaultSpecs) ({ originalConfig ->
                    val wrappedConfig = Slide.Configuration(
                        layoutDirection = originalConfig.layoutDirection,
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

public class SlidePrepareScope(
    public val scope: CoroutineScope,
    public val data: MutableDataMap,
)

public data class Slide(
    public val name: String,
    public val stepCount: Int = 1,
    public val specs: SlideSpecs.(Configuration) -> SlideSpecs = { this },
    public val user: DataMap = emptyDataMap(),
    public val prepare: SlidePrepareScope.() -> Unit = {},
    public val content: @Composable ColumnScope.(Int) -> Unit
) : SlideGroup {
    public data class Configuration(
        val layoutDirection: LayoutDirection,
        val indexInGroup: Int,
        val lastGroupIndex: Int,
    )

    public val lastStep: Int get() = stepCount - 1
    override val slideList: List<Slide> get() = listOf(this)
}

public fun Slide(
    stepCount: Int = 1,
    specs: SlideSpecs.(Slide.Configuration) -> SlideSpecs = { this },
    user: DataMap = emptyDataMap(),
    prepare: SlidePrepareScope.() -> Unit = {},
    content: @Composable ColumnScope.(Int) -> Unit
): PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, Slide>> =
    PropertyDelegateProvider { _, property ->
        val slide = Slide(
            name = property.name,
            stepCount = stepCount,
            specs = specs,
            user = user,
            prepare = prepare,
            content = content
        )
        ReadOnlyProperty { _, _ -> slide }
    }

public val LocalSlidePreparation: ProvidableCompositionLocal<MutableDataMap> = compositionLocalOf { error("Preparation not set.") }