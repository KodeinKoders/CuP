package net.kodein.cup.sa

import kotlinx.coroutines.*
import org.graalvm.polyglot.Context
import java.util.concurrent.Executors


@OptIn(DelicateCoroutinesApi::class)
private class JvmHljs : PlatformHljs {

    private lateinit var ctx: Context

    private val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    private val initJob: Job = GlobalScope.launch(dispatcher) {
        ctx = Context.newBuilder("js")
            .option("engine.WarnInterpreterOnly", "false")
            .build()
        ctx.eval("js", JvmHljs::class.java.getResourceAsStream("/highlight.min.js")!!.reader().use { it.readText() })
    }

    override suspend fun joinInit() { initJob.join() }

    override suspend fun highlight(code: String, language: String): String =
        withContext(dispatcher) {
            ctx.getBindings("js").putMember("code", code)
            ctx.eval("js", "hljs.highlight(code, { language: '$language' }).value").asString()
        }

    override suspend fun listLanguages(): List<String> =
        withContext(dispatcher) {
            val result = ctx.eval("js", "hljs.listLanguages()")
            Array(result.arraySize.toInt()) { result.getArrayElement(it.toLong()).asString() }.asList()
        }
}

internal actual fun PlatformHljs(): PlatformHljs = JvmHljs()
