package net.kodein.cup.utils

import kotlinx.browser.window
import org.w3c.dom.Navigator


private external interface MobileDetectionResult {
    val any: Boolean
}

private external class IsMobileJs {
    fun default(navigator: Navigator): MobileDetectionResult
}

@JsModule("ismobilejs")
private external val isMobileJs: IsMobileJs

internal actual fun isAnyMobile(): Boolean = isMobileJs.default(window.navigator).any
