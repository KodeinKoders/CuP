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
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.chrisjenx.compose2pdf.PdfMargins
import com.chrisjenx.compose2pdf.PdfPageConfig
import com.chrisjenx.compose2pdf.RenderMode
import com.chrisjenx.compose2pdf.renderToPdf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.kodein.cup.FixedPresentationState
import net.kodein.cup.currentSlide
import net.kodein.cup.imgexp.utils.FileDialogMode
import net.kodein.cup.imgexp.utils.fileDialog
import java.awt.Desktop
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectories
import kotlin.io.path.outputStream
import kotlin.math.roundToInt




internal class PdfImageExporter(
    override val format: String,
    val renderMode: RenderMode,
    defaultFileName: String,
) : Exporter {
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
    ) {
        val path = Path(file)
        path.parent.createDirectories()
        withContext(Dispatchers.IO) {
            path.outputStream().use { output ->
                renderToPdf(
                    outputStream = output,
                    config = PdfPageConfig(
                        width = (size.widthIn * 72).dp,
                        height = (size.heightIn * 72).dp,
                        margins = PdfMargins.None,
                    ),
                    density = Density(size.density.toFloat() / 72f),
                    mode = renderMode,
                    pages = states.size,
                ) { index ->
                    val state = states[index]
                    progress(ExportStatus.InProgress("${state.currentSlide.name} - ${state.currentPosition.step}", index.toFloat() / (states.size + 1).toFloat()))
                    FixedCupSlide(
                        width = (size.widthIn * size.density).roundToInt(),
                        height = (size.heightIn * size.density).roundToInt(),
                        state = state,
                    )
                }
            }
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