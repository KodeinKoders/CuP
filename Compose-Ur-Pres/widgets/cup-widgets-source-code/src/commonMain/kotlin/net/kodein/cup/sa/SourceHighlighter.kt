package net.kodein.cup.sa

import androidx.compose.ui.text.TextRange
import kotlinx.collections.immutable.toImmutableList
import net.kodein.cup.PluginCupAPI
import net.kodein.cup.utils.LRUCache


internal interface PlatformHljs {
    suspend fun joinInit()
    suspend fun highlight(code: String, language: String): String
    suspend fun listLanguages(): List<String>
}

internal expect fun PlatformHljs(): PlatformHljs

@PluginCupAPI
public class SourceHighlighter {
    private val hljs = PlatformHljs()

    private val spanRegex = Regex("<span class=\"([^\"]+)\">")

    private val cache = LRUCache<Pair<String, String>, List<ClassesSection>>(
        maxCount = 100,
        maxSize = 128L * 1024L * 1024L,
        initialCount = 100
    )

    public suspend fun joinInit() { hljs.joinInit() }

    public fun getCached(code: String, language: String): List<ClassesSection>? =
        cache.get(Pair(language, code))

    public suspend fun parse(code: String, language: String): List<ClassesSection> {
        hljs.joinInit()
        val sections = ArrayList<ClassesSection>()
        val result = hljs.highlight(code, language)
        var position = 0
        var charIndex = 0
        val stack = ArrayList<Pair<Int, List<String>>>()
        while (position < result.length) {
            when (result[position]) {
                '<' -> {
                    if (result.startsWith("</span>", position)) {
                        val pair = stack.removeLastOrNull() ?: error("Closing span that was not opened at $position:\n${result.substring(position)}")
                        val (start, classes) = pair
                        sections.add(ClassesSection(TextRange(start, charIndex), classes.toImmutableList()))
                        position += "</span>".length
                    }
                    else {
                        val match = spanRegex.matchAt(result, position) ?: error("Non span markup:\n${result.substring(position)}")
                        val classes = match.groupValues[1].split(' ').map { it.removePrefix("hljs-").removeSuffix("_") }
                        stack.add(charIndex to classes)
                        position += match.value.length
                    }
                }
                '&' -> {
                    val i = result.indexOf(';', position)
                    if (i < 0) error("Non character entity:\n${result.substring(position)}")
                    position = i + 1
                    ++charIndex
                }
                else -> {
                    ++position
                    ++charIndex
                }
            }
        }
        sections.sorted()
        cache.put(Pair(language, code), sections, code.length.toLong())
        return sections
    }

    public suspend fun listLanguages(): List<String> {
        hljs.joinInit()
        return hljs.listLanguages()
    }
}
