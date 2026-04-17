package net.kodein.cup.imgexp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NoPhotography
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.kodein.cup.LocalPresentationState
import net.kodein.cup.PresentationPosition
import net.kodein.cup.PresentationState
import net.kodein.cup.config.CupAdditionalOverlay
import net.kodein.cup.config.CupConfigurationBuilder
import net.kodein.cup.config.CupPlugin
import net.kodein.cup.copyFixed
import net.kodein.cup.imgexp.utils.directoryDialog
import net.kodein.cup.utils.CupToolsColors
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import java.awt.Desktop
import java.nio.file.Path
import java.text.DecimalFormat
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteRecursively
import kotlin.io.path.writeBytes


private const val in2mm = 25.4f

@OptIn(ExperimentalPathApi::class)
private suspend fun export(
    state: PresentationState,
    dir: Path,
    widthInch: Float,
    heightInch: Float,
    density: Int,
    exportPngs: Boolean,
    exportPdf: Boolean,
    exporting: (Pair<String, Float>) -> Unit
) {
    dir.deleteRecursively()
    dir.createDirectories()

    val sceneWidth = (widthInch * density).toInt()
    val sceneHeight = (heightInch * density).toInt()

    val toExport = state.slides.flatMapIndexed { slideIndex, slide ->
        val slideExport = slide.context[Export.Key]
        when (slideExport?.type) {
            Export.Type.Only -> (0..<slide.stepCount).filter { it in slideExport.steps }
            Export.Type.Ignore -> (0..<slide.stepCount).filterNot { it in slideExport.steps }
            null -> (0..<slide.stepCount)
        }.map { PresentationPosition(slideIndex, it) }
    }

    (if (exportPdf) PDDocument() else null).use { pdfDocument ->
        toExport.forEachIndexed { index, position ->
            val slide = state.slides[position.slideIndex]
            exporting("${slide.name} - ${position.step}" to (index.toFloat() / (toExport.size + 1).toFloat()))
            val imageState = state.copyFixed().copy(
                currentPosition = position,
                inOverview = false,
                forward = true,
            )
            val png = renderCupSlide(sceneWidth, sceneHeight, imageState)
                ?: error("Could not generate image for${slide.name} - ${position.step}")

            withContext(Dispatchers.IO) {
                if (exportPngs) {
                    dir.resolve("$index-${slide.name}-${position.step}.png").writeBytes(png)
                }

                if (pdfDocument != null) {
                    val pdfPage = PDPage(PDRectangle(widthInch * 72f, heightInch * 72f))
                    val pdfImage = PDImageXObject.createFromByteArray(pdfDocument, png, "$index-${slide.name}-${position.step}.png")
                    PDPageContentStream(pdfDocument, pdfPage).use { stream ->
                        stream.drawImage(pdfImage, 0f, 0f, widthInch * 72f, heightInch * 72f)
                    }
                    pdfDocument.addPage(pdfPage)
                }
            }
        }
        if (pdfDocument != null) {
            exporting("presentation.pdf" to (toExport.size.toFloat() / (toExport.size + 1).toFloat()))
            pdfDocument.save(dir.resolve("presentation.pdf").absolutePathString())
            exporting("presentation.pdf" to 1f)
        } else {
            exporting("PNGs" to 1f)
        }
    }
}

internal data class ExportConfig(
    val dir: Path,
    val widthInch: Float,
    val heightInch: Float,
    val density: Int,
    val pngs: Boolean,
    val pdf: Boolean,
)

@Composable
private fun ExportForm(
    onExport: (ExportConfig) -> Unit
) {
    val scope = rememberCoroutineScope()

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        var dir: String by remember { mutableStateOf(System.getProperty("user.dir") + "/cup-export") }
        var width by remember { mutableStateOf("297") }
        var height by remember { mutableStateOf("210") }
        var unit by remember { mutableStateOf("mm") }
        var density by remember { mutableStateOf("300") }
        var exportPngs by remember { mutableStateOf(false) }
        var exportPdf by remember { mutableStateOf(true) }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = dir,
                onValueChange = { dir = it },
                label = { Text("Export directory") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    scope.launch {
                        val path = directoryDialog("Select export directory")
                        if (path != null) {
                            dir = path.absolutePathString()
                        }
                    }
                }
            ) {
                Text("...")
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = width == "297" && height == "210" && unit == "mm",
                onClick = {
                    width = "297"
                    height = "210"
                    unit = "mm"
                }
            )
            Text("A4")
            Spacer(Modifier.width(24.dp))
            RadioButton(
                selected = width == "11" && height == "8.5" && unit == "in",
                onClick = {
                    width = "11"
                    height = "8.5"
                    unit = "in"
                }
            )
            Text("Letter")
            Spacer(Modifier.width(24.dp))
            RadioButton(
                selected = width == "12" && height == "9" && unit == "in",
                onClick = {
                    width = "12"
                    height = "9"
                    unit = "in"
                }
            )
            Text("4:3")
            Spacer(Modifier.width(24.dp))
            RadioButton(
                selected = width == "16" && height == "9" && unit == "in",
                onClick = {
                    width = "16"
                    height = "9"
                    unit = "in"
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
                value = width,
                onValueChange = { width = it },
                isError = width.toFloatOrNull() == null,
                label = { Text("Width") },
                singleLine = true,
                modifier = Modifier.width(100.dp)
            )
            Text("x")
            OutlinedTextField(
                value = height,
                onValueChange = { height = it },
                isError = height.toFloatOrNull() == null,
                label = { Text("Height") },
                singleLine = true,
                modifier = Modifier.width(100.dp)
            )
            Spacer(Modifier.width(4.dp))
            var expanded by remember { mutableStateOf(false) }
            @OptIn(ExperimentalMaterial3Api::class)
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it },
                modifier = Modifier.width(100.dp)
            ) {
                OutlinedTextField(
                    value = unit,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Unit") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                        .pointerHoverIcon(PointerIcon.Default, overrideDescendants = true)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    val mmFormat = remember { DecimalFormat("0") }
                    DropdownMenuItem(
                        text = {
                            Text("mm")
                        },
                        onClick = {
                            if (unit == "in") {
                                width = width.toFloatOrNull()?.let { it * in2mm }?.let { mmFormat.format(it) } ?: width
                                height = height.toFloatOrNull()?.let { it * in2mm }?.let { mmFormat.format(it) } ?: height
                            }
                            unit = "mm"
                        }
                    )
                    val inFormat = remember { DecimalFormat("0.#") }
                    DropdownMenuItem(
                        text = {
                            Text("in")
                        },
                        onClick = {
                            if (unit == "mm") {
                                width = width.toFloatOrNull()?.let { it / in2mm }?.let { inFormat.format(it) } ?: width
                                height = height.toFloatOrNull()?.let { it / in2mm }?.let { inFormat.format(it) } ?: height
                            }
                            unit = "in"
                        }
                    )
                }
            }
            Text("at")
            OutlinedTextField(
                value = density,
                onValueChange = { density = it },
                isError = density.toIntOrNull() == null,
                label = { Text("Density") },
                singleLine = true,
                modifier = Modifier.width(100.dp)
            )
            Text("dpi")
        }

        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = exportPngs,
                onCheckedChange = { exportPngs = it },
            )
            Text("PNGs")
            Spacer(Modifier.width(24.dp))
            Checkbox(
                checked = exportPdf,
                onCheckedChange = { exportPdf = it },
            )
            Text("PDF")
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                scope.launch {
                    onExport(
                        ExportConfig(
                            dir = Path(dir),
                            widthInch = width.toFloat() / (if (unit == "mm") in2mm else 1f),
                            heightInch = height.toFloat() / (if (unit == "mm") in2mm else 1f),
                            density = density.toInt(),
                            pngs = exportPngs,
                            pdf = exportPdf,
                        )
                    )
                }
            },
            enabled =
                    width.toFloatOrNull() != null && width.toFloat() != 0f
                &&  height.toFloatOrNull() != null && height.toFloat() != 0f
                &&  density.toIntOrNull() != null && density.toInt() != 0
                &&  (exportPngs || exportPdf)
        ) {
            Text("EXPORT")
        }
    }
}

@Composable
private fun ImageExportWindow() {
    val state = LocalPresentationState.current

    val scope = rememberCoroutineScope()
    var exporting: Pair<String, Float>? by remember { mutableStateOf(null) }
    var exportDir: Path by remember { mutableStateOf(Path(".")) }

    if (exporting == null) {
        ExportForm { config ->
            exportDir = config.dir
            scope.launch {
                export(
                    state = state,
                    dir = config.dir,
                    widthInch = config.widthInch,
                    heightInch = config.heightInch,
                    density = config.density,
                    exportPngs = config.pngs,
                    exportPdf = config.pdf,
                ) {
                    exporting = it
                }
            }
        }
    } else {
        val (step, progress) = exporting!!
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = if (progress == 1f) "Exported!" else "Exporting",
                fontSize = 48.sp
            )
            LinearProgressIndicator(
                progress = { progress }
            )
            Text(
                text = step,
                fontSize = 16.sp
            )
            if (progress == 1f) {
                Button(
                    onClick = {
                        Desktop.getDesktop().open(exportDir.toFile())
                    },
                ) {
                    Text("OPEN")
                }
            }
        }
    }
}

internal class ImageExportPlugin : CupPlugin {

    private var isOpen by mutableStateOf(false)

    @Composable
    override fun BoxScope.Content() {
        if (isOpen) {
            Window(
                state = rememberWindowState(width = 720.dp, height = 480.dp),
                title = "Export presentation",
                onCloseRequest = { isOpen = false },
            ) {
                MaterialTheme(colorScheme = CupToolsColors.scheme) {
                    Surface(Modifier.fillMaxSize()) {
                        ImageExportWindow()
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