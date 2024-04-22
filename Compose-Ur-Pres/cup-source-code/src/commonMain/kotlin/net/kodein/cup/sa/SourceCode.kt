package net.kodein.cup.sa

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.zIndex
import net.kodein.cup.sa.utils.*
import kotlin.math.max
import kotlin.math.min


private data class TextPartNode(
    val range: TextRange,
    val type: Type,
    val blockId: SABlock.ID,
    val content: String,
    val children: List<TextPartNode>
) : Comparable<TextPartNode> {
    enum class Type { MultipleLines, OneLine, Text }
    override fun compareTo(other: TextPartNode): Int = this.range.compareTo(other.range)
}

private fun buildTextPartNode(
    range: TextRange,
    fullText: String,
    blockId: SABlock.ID = SABlock.ID.None,
    children: List<TextPartNode> = emptyList(),
): TextPartNode {
    val originalContent = fullText.substring(range)
    val content = originalContent.removeSuffix("\n")
    return TextPartNode(
        range = range,
        type = if ('\n' in content) {
            TextPartNode.Type.MultipleLines
        } else {
            val startsNewLine = range.min == 0 || fullText[range.min - 1] == '\n'
            val endsInNewLine = range.max == fullText.length || fullText[range.max - 1] == '\n'
            if (startsNewLine && endsInNewLine) TextPartNode.Type.OneLine else TextPartNode.Type.Text
        },
        blockId = blockId,
        content = content,
        children = children
    )
}

@Composable
private fun SourceCodePart(
    part: TextPartNode,
    steps: List<SAStep>,
    step: Int,
    textStyle: TextStyle,
    codeStyle: List<StyleSection>,
    isParentHidden: Boolean,
    isParentHighlighted: Boolean,
    partFractions: List<State<Float>>,
    overSpanStyles: Map<Int, Pair<SpanStyle, Float>>
) {
    val currentStep = steps[step]
    val isThisHidden = SAData.State.Hidden in currentStep[part.blockId]
    val visibility by animateFloatAsState(
        targetValue = if (isThisHidden) 0f else 1f,
        animationSpec = tween(600)
    )
    val (scaleX, scaleY) = when (part.type) {
        TextPartNode.Type.MultipleLines, TextPartNode.Type.OneLine -> 1f to visibility
        TextPartNode.Type.Text -> visibility to 1f
    }
    val hasHighlight = currentStep.any { SAData.State.Highlighted in it.value }
    val isHighlighted = SAData.State.Highlighted in currentStep[part.blockId]
    val highlight by animateFloatAsState(
        targetValue = if (hasHighlight && isHighlighted) 1f else 0f,
        animationSpec = tween(600)
    )

    val isHidden = isThisHidden || isParentHidden

    val partStyles = remember(steps) {
        steps.map {
            it[part.blockId]?.filterIsInstance<SAData.State.Styled>()?.map { it.style }
        }
    }

    fun forEachStyle(action: (Int, SAStyle, Float) -> Unit) {
        steps.forEachIndexed { index, _ ->
            val stepFraction by partFractions[index]
            val stepStyles = partStyles[index]
            if (stepFraction > 0 && stepStyles != null) {
                stepStyles.forEachIndexed { styleStep, style ->
                    action(styleStep, style, stepFraction)
                }
            }
        }
    }

    val partOverStyles = HashMap(overSpanStyles)
    forEachStyle { index, saStyle, fraction ->
        partOverStyles[index] = saStyle.spanStyle() to fraction
    }

    Box(
        Modifier
            .scaleWithSize(scaleX, scaleY)
            .alpha(visibility)
            .scale(1f + 0.4f * highlight)
            .zIndex(if (highlight != 0f) 2f else 1f)
            .drawWithContent {
                forEachStyle { _, style, fraction -> with(style) { drawBehind(Rect(Offset.Zero, size), fraction) } }
                drawContent()
                forEachStyle { _, style, fraction -> with(style) { drawOver(Rect(Offset.Zero, size), fraction) } }
            }
    ) {
        if (part.children.isEmpty()) {
            val dim by animateFloatAsState(
                targetValue = if (hasHighlight && !isHighlighted && !isParentHighlighted) 1f else 0f,
                animationSpec = tween(600)
            )
            var partStyle: Pair<SpanStyle, List<StyleSection>> by remember { mutableStateOf(textStyle.toSpanStyle() to emptyList()) }
            LaunchedEffect(codeStyle, isHidden, partOverStyles) {
                if (!isHidden) {
                    var sections = codeStyle.offset(part.range)
                    var fullStyle = textStyle.toSpanStyle()
                    partOverStyles.entries.sortedBy { it.key }.forEach { (_, pair) ->
                        val (style, fraction) = pair
                        sections = sections.map {
                            it.copy(style = lerp(it.style, it.style + style, fraction))
                        }
                        fullStyle = lerp(fullStyle, fullStyle + style, fraction)
                    }
                    partStyle = fullStyle to sections
                }
            }

            Text(
                text = buildAnnotatedString {
                    append(part.content)
                    addStyle(partStyle.first, 0, part.content.length)
                    addStyles(partStyle.second)
                },
                style = textStyle,
                modifier = Modifier
                    .alpha(1f - dim * 0.85f)
            )
        } else {
            val container: @Composable (@Composable () -> Unit) -> Unit = when (part.type) {
                TextPartNode.Type.MultipleLines -> ({ Column { it() } })
                TextPartNode.Type.OneLine, TextPartNode.Type.Text -> ({ Row { it() } })
            }
            container {
                part.children.forEach {
                    SourceCodePart(
                        part = it,
                        steps = steps,
                        step = step,
                        textStyle = textStyle,
                        codeStyle = codeStyle,
                        isParentHidden = isHidden,
                        isParentHighlighted = isHighlighted || isParentHighlighted,
                        partFractions = partFractions,
                        overSpanStyles = partOverStyles,
                    )
                }
            }
        }
    }
}

@Composable
private fun SourceCodeContent(
    sourceCode: SourceCode,
    parts: List<TextPartNode>,
    step: Int,
    style: TextStyle,
    theme: SourceCodeTheme,
) {
    val partFractions = sourceCode.data.steps.mapIndexed { index, _ ->
        animateFloatAsState(
            targetValue = if (index == step) 1f else 0f,
            animationSpec = tween(600)
        )
    }

    Box {
        var codeSize by remember { mutableStateOf(IntSize.Zero) }
        SelectionContainer(
            modifier = Modifier.size(with (LocalDensity.current) { codeSize.toSize().toDpSize() })
        ) {
            Text(
                text = sourceCode.data.fullText - sourceCode.data.hiddenRanges(step),
                style = style.copy(color = Color.Transparent),
                softWrap = false,
            )
        }

        val codeStyle by produceState(emptyList<StyleSection>(), step) { value = sourceCode.sections(step).applySourceCodeTheme(theme) }
        Column(Modifier.onSizeChanged { codeSize = it }) {
            parts.forEach {
                SourceCodePart(
                    part = it,
                    steps = sourceCode.data.steps,
                    step = step,
                    textStyle = style,
                    codeStyle = codeStyle,
                    isParentHidden = false,
                    isParentHighlighted = false,
                    partFractions = partFractions,
                    overSpanStyles = emptyMap()
                )
            }
        }
    }
}

public class SourceCode internal constructor(
    internal val data: SAData,
    internal val sections: suspend (Int) -> List<ClassesSection>
)

@Composable
public fun SourceCode(
    sourceCode: SourceCode,
    step: Int = 0,
    style: TextStyle = LocalDefaultSourceCodeTextStyle.current,
    theme: SourceCodeTheme = LocalDefaultSourceCodeTheme.current,
) {
    remember(sourceCode.data.steps.size, step) {
        if (step > sourceCode.data.steps.lastIndex) {
            println("WARNING: Step $step has not been specified in source code (last known step is ${sourceCode.data.steps.lastIndex}).")
        }
    }

    SourceCodeContent(
        sourceCode = sourceCode,
        parts = remember(sourceCode.data) {
            val lines = sourceCode.data.fullText.lineRanges()
                .filterNot { line -> sourceCode.data.blocks.any { it.range == line } }
                .map { SABlock(SABlock.ID.None, SABlock.Type.Lines, it) }

            (lines + sourceCode.data.blocks).asTree(
                range = SABlock::range,
                node = newNode@ { block, children ->
                    if (children.isEmpty()) {
                        return@newNode buildTextPartNode(block.range, sourceCode.data.fullText, block.id, emptyList())
                    }
                    val addedChildren = ArrayList<TextPartNode>()
                    var lastEnd = block.range.min
                    children.forEach { child ->
                        if (child.range.min != lastEnd) {
                            addedChildren += buildTextPartNode(TextRange(lastEnd, child.range.min), sourceCode.data.fullText)
                        }
                        lastEnd = child.range.max
                    }
                    if (lastEnd != block.range.max) {
                        addedChildren += buildTextPartNode(TextRange(lastEnd, block.range.max), sourceCode.data.fullText)
                    }
                    buildTextPartNode(block.range, sourceCode.data.fullText, block.id, (children + addedChildren).sorted())
                }
            )
        },
        step = min(max(0, step), sourceCode.data.steps.lastIndex),
        style = remember(style, theme) {
            val defaultTheme = theme("default")
            if (defaultTheme != null) style.merge(defaultTheme)
            else style
        },
        theme = theme
    )
}
