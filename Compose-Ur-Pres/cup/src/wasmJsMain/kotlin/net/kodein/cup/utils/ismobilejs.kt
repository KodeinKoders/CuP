package net.kodein.cup.utils

import kotlinx.browser.window
import org.w3c.dom.Navigator


private external interface MobileDetectionResult {
    val any: Boolean
}

@JsModule("ismobilejs")
private external fun isMobile(navigator: Navigator): MobileDetectionResult

internal fun isAnyMobile() = isMobile(window.navigator).any
