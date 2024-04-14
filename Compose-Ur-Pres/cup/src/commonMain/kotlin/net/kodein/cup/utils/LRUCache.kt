package net.kodein.cup.utils

@Suppress("ReplacePutWithAssignment")
public class LRUCache<K : Any, V : Any>(
    public val maxCount: Int,
    public val maxSize: Long,
    initialCount: Int = maxCount / 10
) {
    private class Entry<V>(val value: V, val size: Long)

    private val map = LinkedHashMap<K, Entry<V>>(initialCount)
    private var totalSize: Long = 0

    public fun get(key: K): V? {
        val entry = map.remove(key)
        if (entry != null) {
            map.put(key, entry)
        }
        return entry?.value
    }

    public fun put(key: K, value: V, size: Long) {
        val previous = map.remove(key)
        totalSize -= previous?.size ?: 0

        map.put(key, Entry(value, size))
        totalSize += size

        if (map.count() > maxCount || totalSize > maxSize) {
            val it = map.iterator()
            while (it.hasNext() && map.count() > maxCount || totalSize > maxSize) {
                val removed = it.next()
                it.remove()
                totalSize -= removed.value.size
            }
        }
    }
}
