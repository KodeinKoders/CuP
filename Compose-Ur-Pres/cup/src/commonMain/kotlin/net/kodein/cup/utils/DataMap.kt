package net.kodein.cup.utils


public sealed interface DataMap {
    public interface Key<T : Any>

    public operator fun <T : Any> get(key: Key<T>): T?
    public operator fun contains(key: Key<*>): Boolean

    public fun isEmpty(): Boolean
}

public sealed interface MutableDataMap : DataMap {
    public operator fun <T : Any> set(key: DataMap.Key<T>, value: T)
    public fun <T : Any> getOrPut(key: DataMap.Key<T>, defaultValue: () -> T): T
    public fun <T : Any> remove(key: DataMap.Key<T>): T?
    public fun putAll(other: DataMap)
}

public fun DataMap.isNotEmpty(): Boolean = !isEmpty()

@Suppress("UNCHECKED_CAST")
private class MutableDataMapImpl(initial: DataMap?) : MutableDataMap {

    private val data: MutableMap<DataMap.Key<*>, Any> = when (initial) {
        null, EmptyDataMap -> HashMap()
        is AbstractDataMapEntry<*> -> HashMap<DataMap.Key<*>, Any>().also { it[initial.key] = initial.value }
        is MutableDataMapImpl -> HashMap(initial.data)
    }

    override operator fun <T : Any> get(key: DataMap.Key<T>): T? = data[key] as T?
    override fun <T : Any> getOrPut(key: DataMap.Key<T>, defaultValue: () -> T): T = data.getOrPut(key, defaultValue) as T
    override operator fun <T : Any> set(key: DataMap.Key<T>, value: T) { data[key] = value }
    override operator fun contains(key: DataMap.Key<*>): Boolean = key in data
    override fun <T : Any> remove(key: DataMap.Key<T>): T? = data.remove(key) as T?
    override fun isEmpty(): Boolean = data.isEmpty()

    override fun putAll(other: DataMap) {
        when (other) {
            EmptyDataMap -> {}
            is AbstractDataMapEntry<*> -> data[other.key] = other.value
            is MutableDataMapImpl -> data.putAll(other.data)
        }
    }
}

public fun MutableDataMap(initial: DataMap? = null): MutableDataMap = MutableDataMapImpl(initial)

private data object EmptyDataMap : DataMap {
    override fun <T : Any> get(key: DataMap.Key<T>): T? = null
    override fun contains(key: DataMap.Key<*>): Boolean = false
    override fun isEmpty(): Boolean = true
}

public fun emptyDataMap(): DataMap = EmptyDataMap

public abstract class AbstractDataMapEntry<T : Any> : DataMap {
    public abstract val key: DataMap.Key<T>
    public abstract val value: T

    @Suppress("UNCHECKED_CAST")
    final override fun <T : Any> get(key: DataMap.Key<T>): T? =
        if (this.key == key) value as T else null

    final override fun contains(key: DataMap.Key<*>): Boolean =
        this.key == key

    final override fun isEmpty(): Boolean = false

}

public abstract class DataMapElement<T : Any>(
    override val key: DataMap.Key<T>
) : AbstractDataMapEntry<T>() {
    @Suppress("UNCHECKED_CAST")
    final override val value: T get() = this as T
}

public data class DataMapEntry<T : Any>(
    override val key: DataMap.Key<T>,
    override val value: T
) : AbstractDataMapEntry<T>()

public infix fun <T : Any> DataMap.Key<T>.sets(value: T): DataMapEntry<T> = DataMapEntry(this, value)

public fun dataMapOf(vararg entries: AbstractDataMapEntry<*>): DataMap {
    val map = MutableDataMap()
    entries.forEach {
        @Suppress("UNCHECKED_CAST")
        it as AbstractDataMapEntry<Any>
        map[it.key] = it.value
    }
    return map
}

public operator fun DataMap.plus(other: DataMap): DataMap =
    MutableDataMap(this).apply { putAll(other) }
