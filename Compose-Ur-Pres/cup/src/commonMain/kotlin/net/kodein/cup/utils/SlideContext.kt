package net.kodein.cup.utils

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList


public sealed interface SlideContext {
    @Immutable
    public interface Key<T : Any>

    public operator fun <T : Any> get(key: Key<T>): T?
}

@Deprecated("Renamed SlideContext", ReplaceWith("SlideContext"), DeprecationLevel.ERROR)
public typealias DataMap = SlideContext

public data object EmptySlideContext : SlideContext {
    override fun <T : Any> get(key: SlideContext.Key<T>): T? = null
}

@Deprecated("Renamed EmptySlideContext", ReplaceWith("EmptySlideContext"), DeprecationLevel.ERROR)
public typealias EmptyDataMap = EmptySlideContext

public operator fun SlideContext.contains(key: SlideContext.Key<*>): Boolean = get(key) != null

@Suppress("UNCHECKED_CAST")
private class CompositeSlideContext(
    val contexts: ImmutableList<SlideContext>
) : SlideContext {
    override operator fun <T : Any> get(key: SlideContext.Key<T>): T? = contexts.firstNotNullOfOrNull { it[key] }
}

public fun slideContextOf(vararg contexts: SlideContext): SlideContext = when (contexts.size) {
    0 -> EmptySlideContext
    1 -> contexts[0]
    else -> CompositeSlideContext(contexts.toImmutableList())
}

public operator fun SlideContext.plus(other: SlideContext): SlideContext =
    slideContextOf(this, other)

public fun slideContextOf(contexts: List<SlideContext>): SlideContext = when (contexts.size) {
    0 -> EmptySlideContext
    1 -> contexts[0]
    else -> CompositeSlideContext(contexts.toImmutableList())
}

@Deprecated("Renamed slideContextOf", ReplaceWith("slideContextOf"), DeprecationLevel.ERROR)
public fun dataMapOf(vararg entries: AbstractDataMapEntry<*>): SlideContext =
    slideContextOf(*entries)

public abstract class AbstractSlideContextEntry<T : Any> : SlideContext {
    public abstract val key: SlideContext.Key<T>
    public abstract val value: T

    @Suppress("UNCHECKED_CAST")
    final override fun <T : Any> get(key: SlideContext.Key<T>): T? =
        if (this.key == key) value as T else null
}

@Deprecated("Renamed AbstractSlideContextEntry", ReplaceWith("AbstractSlideContextEntry"), DeprecationLevel.ERROR)
public typealias AbstractDataMapEntry<T> = AbstractSlideContextEntry<T>

public abstract class SlideContextElement<T : Any>(
    override val key: SlideContext.Key<T>
) : AbstractSlideContextEntry<T>() {
    @Suppress("UNCHECKED_CAST")
    final override val value: T get() = this as T
}

@Deprecated("Renamed SlideContextElement", ReplaceWith("SlideContextElement<T>"), DeprecationLevel.ERROR)
public typealias DataMapElement<T> = SlideContextElement<T>

public data class SlideContextEntry<T : Any>(
    override val key: SlideContext.Key<T>,
    override val value: T
) : AbstractSlideContextEntry<T>()

@Deprecated("Renamed SlideContextEntry", ReplaceWith("SlideContextEntry"), DeprecationLevel.ERROR)
public typealias DataMapEntry<T> = SlideContextEntry<T>

public infix fun <T : Any> SlideContext.Key<T>.sets(value: T): SlideContextEntry<T> = SlideContextEntry(this, value)
