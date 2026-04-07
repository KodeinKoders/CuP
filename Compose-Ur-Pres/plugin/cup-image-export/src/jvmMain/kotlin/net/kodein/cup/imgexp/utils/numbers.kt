package net.kodein.cup.imgexp.utils

import kotlin.math.pow
import kotlin.math.roundToInt


internal fun Float.roundToDecimals(decimals: Int): Float {
    if (decimals == 0) return roundToInt().toFloat()
    val multiplier = 10f.pow(decimals)
    return (this * multiplier).roundToInt().toFloat() / multiplier
}
