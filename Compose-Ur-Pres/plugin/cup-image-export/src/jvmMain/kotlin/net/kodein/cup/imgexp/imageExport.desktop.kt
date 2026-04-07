package net.kodein.cup.imgexp

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NoPhotography
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material3.Button
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
import androidx.compose.runtime.*
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
import net.kodein.cup.imgexp.utils.roundToDecimals
import net.kodein.cup.utils.CupToolsColors
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import java.awt.Desktop
import java.nio.file.Path
import kotlin.io.path.*


private const val in2mm = 25.4f

@OptIn(ExperimentalPathApi::class)
private suspend fun export(
    state: PresentationState,
    dir: Path,
    widthInch: Float,
    heightInch: Float,
    dpi: Int,
    exporting: (Pair<String, Float>) -> Unit
) {
    dir.deleteRecursively()
    dir.createDirectories()

    val sceneWidth = (widthInch * dpi).toInt()
    val sceneHeight = (heightInch * dpi).toInt()

    PDDocument().use { pdfDocument ->
        val toExport = state.slides.flatMapIndexed { slideIndex, slide ->
            val slideExport = slide.user[Export.Key]
            when (slideExport?.type) {
                Export.Type.Only -> (0..<slide.stepCount).filter { it in slideExport.steps }
                Export.Type.Ignore -> (0..<slide.stepCount).filterNot { it in slideExport.steps }
                null -> (0..<slide.stepCount)
            }.map { PresentationPosition(slideIndex, it) }
        }

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
                val out = dir.resolve("$index-${slide.name}-${position.step}.png")
                out.writeBytes(png)

                val pdfPage = PDPage(PDRectangle(widthInch * 72f, heightInch * 72f))
                val pdfImage = PDImageXObject.createFromByteArray(pdfDocument, png, "$index-${slide.name}-${position.step}.png")
                PDPageContentStream(pdfDocument, pdfPage).use { stream ->
                    stream.drawImage(pdfImage, 0f, 0f, widthInch * 72f, heightInch * 72f)
                }
                pdfDocument.addPage(pdfPage)
            }
        }
        exporting("presentation.pdf" to (toExport.size.toFloat() / (toExport.size + 1).toFloat()))
        pdfDocument.save(dir.resolve("presentation.pdf").absolutePathString())
        exporting("presentation.pdf" to 1f)
    }
}

@Composable
private fun ExportForm(
    onExport: (Path, Float, Float, Int) -> Unit
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
            Spacer(Modifier.width(16.dp))
            RadioButton(
                selected = width == "11" && height == "8.5" && unit == "in",
                onClick = {
                    width = "11"
                    height = "8.5"
                    unit = "in"
                }
            )
            Text("Letter")
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
                    DropdownMenuItem(
                        text = {
                            Text("mm")
                        },
                        onClick = {
                            if (unit == "in") {
                                width = width.toFloatOrNull()?.let { it * in2mm }?.roundToDecimals(1)?.toString() ?: width
                                height = height.toFloatOrNull()?.let { it * in2mm }?.roundToDecimals(1)?.toString() ?: height
                            }
                            unit = "mm"
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text("in")
                        },
                        onClick = {
                            if (unit == "mm") {
                                width = width.toFloatOrNull()?.let { it / in2mm }?.roundToDecimals(2)?.toString() ?: width
                                height = height.toFloatOrNull()?.let { it / in2mm }?.roundToDecimals(2)?.toString() ?: height
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

        Button(
            onClick = {
                scope.launch {
                    onExport(
                        Path(dir),
                        width.toFloat() / (if (unit == "mm") in2mm else 1f),
                        height.toFloat() / (if (unit == "mm") in2mm else 1f),
                        density.toInt()
                    )
                }
            },
            enabled = width.toFloatOrNull() != null && height.toFloatOrNull() != null && density.toIntOrNull() != null
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
        ExportForm { dir, widthInch, heightInch, dpi ->
            exportDir = dir
            scope.launch {
                export(state, dir, widthInch, heightInch, dpi) {
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