package net.kodein.cup.imgexp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.kodein.cup.FixedPresentationState
import net.kodein.cup.currentSlide
import net.kodein.cup.imgexp.utils.FileDialogMode
import net.kodein.cup.imgexp.utils.fileDialog
import net.kodein.cup.imgexp.utils.renderSceneToPng
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import java.awt.Desktop
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectories
import kotlin.io.path.name
import kotlin.math.roundToInt


internal class PdfImageExporter(
    defaultFileName: String,
) : Exporter {

    override val format get() = "PDF"

    private var file: String by mutableStateOf(System.getProperty("user.dir") + "/cup-export/$defaultFileName")

    @Composable
    override fun ColumnScope.Form() {
        val scope = rememberCoroutineScope()

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = file,
                onValueChange = { file = it },
                label = { Text("PDF File") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    scope.launch {
                        val path = fileDialog("Select export directory", FileDialogMode.SAVE_FILE)
                        if (path != null) {
                            file = path.absolutePathString()
                        }
                    }
                }
            ) {
                Text("...")
            }
        }
    }

    override suspend fun export(
        size: Exporter.Size,
        states: List<FixedPresentationState>,
        progress: (ExportStatus.InProgress) -> Unit,
    ) = withContext(Dispatchers.IO) {
        val path = Path(file)
        path.parent.createDirectories()

        val sceneWidth = (size.widthIn * size.density).roundToInt()
        val sceneHeight = (size.heightIn * size.density).roundToInt()

        PDDocument().use { pdfDocument ->
            states.forEachIndexed { index, state ->
                progress(ExportStatus.InProgress("${state.currentSlide.name} - ${state.currentPosition.step}", index.toFloat() / (states.size + 1).toFloat()))
                val png = renderSceneToPng(sceneWidth, sceneHeight) { FixedCupSlide(sceneWidth, sceneHeight, state) }
                val pdfPage = PDPage(PDRectangle(size.widthIn * 72f, size.heightIn * 72f))
                val pdfImage = PDImageXObject.createFromByteArray(pdfDocument, png, "${state.currentPosition.slideIndex}-${state.currentSlide.name}-${state.currentPosition.step}.png")
                PDPageContentStream(pdfDocument, pdfPage).use { it.drawImage(pdfImage, 0f, 0f, size.widthIn * 72f, size.heightIn * 72f) }
                pdfDocument.addPage(pdfPage)
            }
            progress(ExportStatus.InProgress(path.name, states.size.toFloat() / (states.size + 1).toFloat()))
            pdfDocument.save(path.absolutePathString())
            progress(ExportStatus.InProgress(path.name, 1f))
        }
    }

    @Composable
    override fun ColumnScope.Done() {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    Desktop.getDesktop().open(File(file))
                },
            ) {
                Text("OPEN PDF")
            }
            Button(
                onClick = {
                    Desktop.getDesktop().open(File(file).parentFile)
                },
            ) {
                Text("OPEN DIRECTORY")
            }
        }
    }

    override val isFormValid: Boolean get() = file.isNotBlank()
}