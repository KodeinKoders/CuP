@file:OptIn(ExperimentalMaterial3Api::class)

package net.kodein.cup.imgexp

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.NoPhotography
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import com.chrisjenx.compose2pdf.RenderMode
import kotlinx.coroutines.launch
import net.kodein.cup.LocalPresentationState
import net.kodein.cup.PresentationPosition
import net.kodein.cup.PresentationState
import net.kodein.cup.Slide
import net.kodein.cup.config.CupAdditionalOverlay
import net.kodein.cup.config.CupConfigurationBuilder
import net.kodein.cup.config.CupPlugin
import net.kodein.cup.copyFixed
import net.kodein.cup.utils.CupToolsMaterialTheme
import java.text.DecimalFormat


private class SizeState {
    var width by mutableStateOf("297")
    var height by mutableStateOf("210")
    var density by mutableStateOf("300")
    var unit by mutableStateOf(Unit.MM)

    enum class Unit(val inInch: Float, format: String) {
        MM(25.4f, "0"),
        IN(1f, "0.#"),
        ;
        val format = DecimalFormat(format)
    }

    fun convertTo(value: Unit) {
        if (value != unit) {
            width = width.toFloatOrNull()?.let { it * value.inInch / unit.inInch }?.let { value.format.format(it) } ?: width
            height = height.toFloatOrNull()?.let { it * value.inInch / unit.inInch }?.let { value.format.format(it) } ?: height
        }
        unit = value
    }

    val isFormValid: Boolean
        get() = width.toFloatOrNull() != null && width.toFloat() != 0f
                &&  height.toFloatOrNull() != null && height.toFloat() != 0f
                &&  density.toIntOrNull() != null && density.toInt() != 0
}

@Composable
private fun SizeForm(
    state: SizeState,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = state.width == "297" && state.height == "210" && state.unit == SizeState.Unit.MM,
            onClick = {
                state.width = "297"
                state.height = "210"
                state.unit = SizeState.Unit.MM
            }
        )
        Text("A4")
        Spacer(Modifier.width(24.dp))
        RadioButton(
            selected = state.width == "11" && state.height == "8.5" && state.unit == SizeState.Unit.IN,
            onClick = {
                state.width = "11"
                state.height = "8.5"
                state.unit = SizeState.Unit.IN
            }
        )
        Text("Letter")
        Spacer(Modifier.width(24.dp))
        RadioButton(
            selected = state.width == "12" && state.height == "9" && state.unit == SizeState.Unit.IN,
            onClick = {
                state.width = "12"
                state.height = "9"
                state.unit = SizeState.Unit.IN
            }
        )
        Text("4:3")
        Spacer(Modifier.width(24.dp))
        RadioButton(
            selected = state.width == "16" && state.height == "9" && state.unit == SizeState.Unit.IN,
            onClick = {
                state.width = "16"
                state.height = "9"
                state.unit = SizeState.Unit.IN
            }
        )
        Text("16:9")
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = state.width,
            onValueChange = { state.width = it },
            isError = state.width.toFloatOrNull() == null,
            label = { Text("Width") },
            singleLine = true,
            modifier = Modifier.width(100.dp)
        )
        Text("x")
        OutlinedTextField(
            value = state.height,
            onValueChange = { state.height = it },
            isError = state.height.toFloatOrNull() == null,
            label = { Text("Height") },
            singleLine = true,
            modifier = Modifier.width(100.dp)
        )
        Spacer(Modifier.width(4.dp))
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.width(100.dp)
        ) {
            OutlinedTextField(
                value = state.unit.name.lowercase(),
                onValueChange = {},
                readOnly = true,
                label = { Text("Unit") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .pointerHoverIcon(PointerIcon.Default, overrideDescendants = true)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("mm") },
                    onClick = { state.convertTo(SizeState.Unit.MM) ; expanded = false }
                )
                DropdownMenuItem(
                    text = { Text("in") },
                    onClick = { state.convertTo(SizeState.Unit.IN) ; expanded = false }
                )
            }
        }
        Text("at")
        OutlinedTextField(
            value = state.density,
            onValueChange = { state.density = it },
            isError = state.density.toIntOrNull() == null,
            label = { Text("Density") },
            singleLine = true,
            modifier = Modifier.width(100.dp)
        )
        Text("dpi")
    }
}

private class ExporterState(
    val allExporters: List<Exporter>,
    allSlides: List<Slide>,
) {
    var exporter: Exporter by mutableStateOf(allExporters.first())
    var exportedSlides: Set<Int> by mutableStateOf(allSlides.indices.toSet())
}

@Composable
private fun ColumnScope.ExporterForm(
    exporterState: ExporterState,
) {
    Row {
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.width(256.dp)
        ) {
            OutlinedTextField(
                value = exporterState.exporter.format,
                onValueChange = {},
                readOnly = true,
                label = { Text("As") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .pointerHoverIcon(PointerIcon.Default, overrideDescendants = true)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                exporterState.allExporters.forEach {
                    DropdownMenuItem(
                        text = { Text(it.format) },
                        onClick = { exporterState.exporter = it ; expanded = false }
                    )
                }
            }
        }
    }
    with (exporterState.exporter) { Form() }
}

internal sealed interface ExportStatus {
    data object Configuring : ExportStatus
    data class InProgress(val step: String?, val progress: Float) : ExportStatus
    data object Finished : ExportStatus
}

@Composable
private fun ImageExportWindow(
    presentationState: PresentationState,
    sizeState: SizeState,
    exporterState: ExporterState,
    export: () -> Unit,
    exportStatus: ExportStatus,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        when (exportStatus) {
            ExportStatus.Configuring -> {
                Row {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(8.dp).weight(1f)
                    ) {
                        SizeForm(sizeState)
                        Spacer(Modifier.height(24.dp))
                        ExporterForm(exporterState)
                        Spacer(Modifier.height(24.dp))
                        Button(
                            onClick = { export() },
                            enabled = sizeState.isFormValid && exporterState.exporter.isFormValid
                        ) {
                            Text("EXPORT")
                        }
                    }
                    VerticalDivider()
                    Box(
                        modifier = Modifier
                            .width(192.dp)
                            .fillMaxHeight()
                    ) {
                        val scrollState = rememberScrollState()
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                TriStateCheckbox(
                                    state = when {
                                        exporterState.exportedSlides.isEmpty() -> ToggleableState.Off
                                        exporterState.exportedSlides == presentationState.slides.indices.toSet() -> ToggleableState.On
                                        else -> ToggleableState.Indeterminate
                                    },
                                    onClick = {
                                        if (exporterState.exportedSlides == presentationState.slides.indices.toSet()) {
                                            exporterState.exportedSlides = emptySet()
                                        } else {
                                            exporterState.exportedSlides = presentationState.slides.indices.toSet()
                                        }
                                    }
                                )
                                Text("All slides")
                            }
                            HorizontalDivider(Modifier.padding(horizontal = 8.dp))
                            LocalPresentationState.current.slides.forEachIndexed { slideIndex, slide ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = slideIndex in exporterState.exportedSlides,
                                        onCheckedChange = {
                                            exporterState.exportedSlides = if (it) exporterState.exportedSlides + slideIndex else exporterState.exportedSlides - slideIndex
                                        }
                                    )
                                    Column {
                                        Text("${slideIndex + 1}. ${slide.name}")
                                        val stepCount = slide.getExportedSteps().count()
                                        Text(
                                            text = "$stepCount step${if (stepCount != 1) "s" else ""}",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                        VerticalScrollbar(
                            adapter = rememberScrollbarAdapter(scrollState),
                            modifier = Modifier.align(Alignment.CenterEnd)
                        )
                    }
                }
            }
            is ExportStatus.InProgress -> {
                Text(
                    text = exporterState.exporter.format,
                    fontSize = 48.sp,
                )
                LinearProgressIndicator(
                    progress = { exportStatus.progress }
                )
                Text(
                    text = exportStatus.step ?: " ",
                    fontSize = 16.sp
                )
            }
            ExportStatus.Finished -> {
                Text(
                    text = exporterState.exporter.format,
                    fontSize = 48.sp,
                )
                with(exporterState.exporter) { Done() }
            }
        }
    }
}

internal class ImageExportPlugin : CupPlugin {

    private var isOpen by mutableStateOf(false)

    private val sizeState = SizeState()

    private suspend fun export(
        presentationState: PresentationState,
        exporterState: ExporterState,
        setStatus: (ExportStatus) -> Unit,
    ) {
        setStatus(ExportStatus.InProgress(null, 0f))
        exporterState.exporter.export(
            size = Exporter.Size(
                widthIn = sizeState.width.toFloat() / sizeState.unit.inInch,
                heightIn = sizeState.height.toFloat() / sizeState.unit.inInch,
                density = sizeState.density.toInt(),
            ),
            states = exporterState.exportedSlides.flatMap { slideIndex ->
                val slide = presentationState.slides[slideIndex]
                slide.getExportedSteps().map {
                    presentationState.copyFixed(
                        currentPosition = PresentationPosition(slideIndex, it),
                        forward = true,
                    )
                }
            },
            progress = setStatus,
        )
        setStatus(ExportStatus.Finished)
    }

    @Composable
    override fun BoxScope.Content() {
        if (isOpen) {
            Window(
                state = rememberWindowState(width = 720.dp, height = 480.dp),
                title = "Export presentation",
                onCloseRequest = { isOpen = false },
            ) {
                var exportStatus: ExportStatus by remember { mutableStateOf(ExportStatus.Configuring) }
                val presentationState by rememberUpdatedState(LocalPresentationState.current)
                val exporterState = remember(presentationState.slides) {
                    ExporterState(
                        allExporters = listOf(
                            PngExporter(),
                            PdfImageExporter("PDF", RenderMode.RASTER, "presentation.pdf"),
                            // At the moment, vector mode produces bad results, so it is disabled.
                            // Compared to Raster mode:
                            // - It does not render Lottie animated images first frame.
                            // - It does not support platform emoji (in Demo slide 1, using TextWithPlatformEmoji instead of TextWithNotoImageEmoji makes the renderer crash).
                            // - It does not support XML vectors (in Demo, the Kodein logo is not displayed behind all slides, and in slide 8, the Kodein logo is not shown).
                            // - It ignores icon tint (in Demo, all icons are black when they should be white-ish).
                            // - It does not support multiple fonts (the Compose2Pdf API only supports one font family).
                            // PdfImageExporter("Vector PDF", RenderMode.VECTOR, "presentation.pdf"),
                        ),
                        allSlides = presentationState.slides,
                    )
                }
                val scope = rememberCoroutineScope()

                CupToolsMaterialTheme {
                    Surface(Modifier.fillMaxSize()) {
                        ImageExportWindow(
                            presentationState = presentationState,
                            sizeState = sizeState,
                            exporterState = exporterState,
                            export = {
                                scope.launch {
                                    export(
                                        presentationState = presentationState,
                                        exporterState = exporterState,
                                        setStatus = { exportStatus = it }
                                    )
                                }
                            },
                            exportStatus = exportStatus,
                        )
                    }
                }
            }
        }
    }

    override fun overlay(state: PresentationState): List<CupAdditionalOverlay> = listOf(
        CupAdditionalOverlay(
            text = "Export",
            onClick = { isOpen = !isOpen },
            icon = if (isOpen) Icons.Rounded.NoPhotography else Icons.Rounded.PhotoCamera,
            inMenu = true
        )
    )
}


public actual fun CupConfigurationBuilder.imageExport() {
    plugin(ImageExportPlugin())
}