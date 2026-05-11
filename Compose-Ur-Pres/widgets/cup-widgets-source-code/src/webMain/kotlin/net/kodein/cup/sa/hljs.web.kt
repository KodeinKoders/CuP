@file:OptIn(ExperimentalWasmJsInterop::class, ExperimentalWasmJsInterop::class)

package net.kodein.cup.sa

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.JsArray
import kotlin.js.JsModule
import kotlin.js.JsString
import kotlin.js.get
import kotlin.js.js
import kotlin.js.length
import kotlin.js.toJsString

private external interface JsHljsResult {
    val value: JsString
}

private external interface JsHljs {
    fun highlight(code: JsString, options: JsAny): JsHljsResult
    fun listLanguages(): JsArray<JsString>
}

private fun createHighlightOptions(@Suppress("UNUSED_PARAMETER") language: String): JsAny =
    js("({ language: language })")

@JsModule("highlight.js")
private external val hljs : JsHljs

private class WasmHljs : PlatformHljs {
    override suspend fun joinInit() {}

    override suspend fun highlight(code: String, language: String): String =
        hljs.highlight(code.toJsString(), createHighlightOptions(language)).value.toString()

    override suspend fun listLanguages(): List<String> {
        val jsArray = hljs.listLanguages()
        return Array(jsArray.length) { jsArray[it].toString() }.toList()
    }
}

internal actual fun PlatformHljs(): PlatformHljs = WasmHljs()
