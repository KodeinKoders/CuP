package net.kodein.cup.sa

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.zIndex
import kotlinx.collections.immutable.*
import kotlinx.coroutines.Deferred
import net.kodein.cup.sa.utils.*


@Stable
private data class TextPartNode(
    val range: TextRange,
    val type: Type,
    val block: SABlock?,
    val content: String,
    val children: ImmutableList<TextPartNode>
) : Comparable<TextPartNode> {
    val contentRange: TextRange get() = TextRange(range.start, range.start + content.length)
    enum class Type { MultipleLines, OneLine, Text }
    override fun compareTo(other: TextPartNode): Int = this.range.compareTo(other.range)
}

private fun buildTextPartNode(
    range: TextRange,
    fullText: String,
    block: SABlock? = null,
    children: ImmutableList<TextPartNode> = persistentListOf(),
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
        block = block,
        content = content,
        children = children
    )
}

public data class SourceCodeDebugColors(
    val multipleLines: Color = Color.Magenta,
    val oneLine: Color = Color.Red,
    val text: Color = Color.Green
)

private fun SourceCodeDebugColors.colorFor(type: TextPartNode.Type) =
    when (type) {
        TextPartNode.Type.MultipleLines -> multipleLines
        TextPartNode.Type.OneLine -> oneLine
        TextPartNode.Type.Text -> text
    }

@Composable
private fun SourceCodePart(
    part: TextPartNode,
    steps: ImmutableList<SAStep>,
    step: Int,
    textStyle: TextStyle,
    codeStyle: ImmutableList<StyleSection>,
    isParentHidden: Boolean,
    isParentHighlighted: Boolean,
//    partFractions: ImmutableList<State<Float>>,
    partFractions: (Int) -> Float,
    overSpanStyles: ImmutableMap<Int, Pair<SpanStyle, Float>>,
    debug: SourceCodeDebugColors?
) {
    val blockId = part.block?.id ?: SABlock.ID.None
    val currentStep = steps[step]
    val isThisHidden = SAData.State.Hidden in currentStep[blockId]
    val visibility by animateFloatAsState(
        targetValue = if (isThisHidden) 0f else 1f,
        animationSpec = tween(600)
    )
    val (scaleX, scaleY) = when (part.type) {
        TextPartNode.Type.MultipleLines, TextPartNode.Type.OneLine -> 1f to visibility
        TextPartNode.Type.Text -> visibility to 1f
    }
    val hasHighlight = currentStep.any { SAData.State.Highlighted in it.value }
    val isHighlighted = SAData.State.Highlighted in currentStep[blockId]
    val highlight by animateFloatAsState(
        targetValue = if (hasHighlight && isHighlighted) 1f else 0f,
        animationSpec = tween(600)
    )

    val isHidden = isThisHidden || isParentHidden

    val partStyles = remember(steps) {
        steps.map {
            it[blockId]?.filterIsInstance<SAData.State.Styled>()?.map { it.style }
        }
    }

    fun forEachStyle(action: (Int, SAStyle, Float) -> Unit) {
        steps.forEachIndexed { index, _ ->
            val stepFraction = partFractions(index)
            val stepStyles = partStyles[index]
            if (stepFraction > 0 && stepStyles != null) {
                stepStyles.forEachIndexed { styleStep, style ->
                    action(styleStep, style, stepFraction)
                }
            }
        }
    }

    val partOverStyles = overSpanStyles.toPersistentMap().builder().apply {
        forEachStyle { index, saStyle, fraction ->
            put(index, saStyle.spanStyle() to fraction)
        }
    }.build()

    Box(
        Modifier
            .zIndex(if (highlight != 0f) 2f else 1f)
            .scale(1f + 0.4f * highlight)
            .then(
                if (debug != null && part.block != null && part.block.id != SABlock.ID.None) {
                    val textMeasurer = rememberTextMeasurer()
                    val dim by animateFloatAsState(
                        targetValue = if (hasHighlight && !isHighlighted && !isParentHighlighted) 1f else 0f,
                        animationSpec = tween(600)
                    )
                    Modifier
                        .padding(0.5.dp)
                        .drawWithContent {
                            drawContent()
                            drawText(
                                textMeasurer = textMeasurer,
                                text = part.block.debugName,
                                maxLines = 1,
                                softWrap = false,
                                style = TextStyle(
                                    color = Color.White.copy(alpha = 1f - dim),
                                    fontSize = 4.sp,
                                    lineHeight = 4.sp,
                                    background = lerp(debug.colorFor(part.type), Color.Black, 0.5f).copy(alpha = 1f - dim),
                                )
                            )
                        }
                        .border(
                            width = 1.dp,
                            color = debug.colorFor(part.type).copy(alpha = 1f - dim * 0.85f)
                        )
                        .padding(1.dp)
                        .padding(top = (3 * visibility).dp)
                } else Modifier
            )
            .alpha(visibility)
            .scaleWithSize(scaleX, scaleY)
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

            LaunchedEffect(textStyle, codeStyle, isHidden, partOverStyles) {
                if (!isHidden) {
                    var sections = codeStyle.offset(part.contentRange)
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
                maxLines = 1,
                softWrap = false,
                modifier = Modifier
                    .alpha(1f - dim * 0.85f)
            )
        } else {
            val container: @Composable (@Composable () -> Unit) -> Unit = when (part.type) {
                TextPartNode.Type.MultipleLines -> ({ Column { it() } })
                TextPartNode.Type.OneLine, TextPartNode.Type.Text -> ({ Row(verticalAlignment = Alignment.CenterVertically) { it() } })
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
                        debug = debug
                    )
                }
            }
        }
    }
}

@Composable
private fun SourceCodeContent(
    sourceCode: SourceCode,
    parts: ImmutableList<TextPartNode>,
    step: Int,
    style: TextStyle,
    theme: SourceCodeTheme,
    debug: SourceCodeDebugColors?,
    modifier: Modifier = Modifier
) {
    val partFractions = sourceCode.data.steps.mapIndexed { index, _ ->
        animateFloatAsState(
            targetValue = if (index == step) 1f else 0f,
            animationSpec = tween(600)
        )
    }

    Box(modifier) {
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

        val codeStyle by produceState(persistentListOf(), step) { value = sourceCode.sections(step).await().applySourceCodeTheme(theme).toImmutableList() }
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
//                    partFractions = partFractions,
                    partFractions = { partFractions[it].value },
                    overSpanStyles = persistentMapOf(),
                    debug = debug
                )
            }
        }
    }
}

public class SourceCode internal constructor(
    internal val data: SAData,
    internal val sections: (Int) -> Deferred<List<ClassesSection>>
)

@Composable
public fun SourceCode(
    sourceCode: SourceCode,
    step: Int = 0,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle(fontFamily = FontFamily.Monospace),
    theme: SourceCodeTheme = SourceCodeThemes.intelliJLight,
    debug: SourceCodeDebugColors? = null
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
                .map { SABlock(SABlock.ID.None, SABlock.Type.Lines, it, "") }

            (lines + sourceCode.data.blocks).asTree<SABlock, TextPartNode>(
                range = SABlock::range,
                node = newNode@ { block, children ->
                    if (children.isEmpty()) {
                        return@newNode buildTextPartNode(block.range, sourceCode.data.fullText, block, persistentListOf())
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
                    buildTextPartNode(block.range, sourceCode.data.fullText, block, (children + addedChildren).sorted().toPersistentList())
                }
            ).toImmutableList()
        },
        step = step.coerceIn(0..sourceCode.data.steps.lastIndex),
        style = remember(style, theme) {
            val defaultTheme = theme("default")
            if (defaultTheme != null) style.merge(defaultTheme)
            else style
        },
        theme = theme,
        modifier = modifier,
        debug = debug
    )
}
