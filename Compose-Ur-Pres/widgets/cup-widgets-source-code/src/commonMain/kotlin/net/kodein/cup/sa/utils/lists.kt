package net.kodein.cup.sa.utils


internal operator fun <E> List<E>?.contains(element: E): Boolean =
    if (this != null) element in this
    else false
