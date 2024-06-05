package net.kodein.cup.utils

import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


public typealias EagerProperty<V> = PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, V>>

public inline fun <V> eagerProperty(crossinline builder: (KProperty<*>) -> V): EagerProperty<V> =
    PropertyDelegateProvider { _, prop ->
        val value = builder(prop)
        ReadOnlyProperty { _, _ -> value }
    }
