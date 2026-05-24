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
import net.kodein.cup.imgexp.Exporter.Size
import net.kodein.cup.imgexp.utils.FileDialogMode
import net.kodein.cup.imgexp.utils.fileDialog
import net.kodein.cup.imgexp.utils.renderSceneToPng
import java.awt.Desktop
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectories
import kotlin.io.path.writeBytes
import kotlin.math.roundToInt
import kotlin.time.ExperimentalTime


internal class PngExporter : Exporter {

    override val format get() = "PNGs"

    private var dir: String by mutableStateOf(System.getProperty("user.dir") + "/cup-export/presentation-images")

    @Composable
    override fun ColumnScope.Form() {
        val scope = rememberCoroutineScope()

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
                        val path = fileDialog("Select export directory", FileDialogMode.LOAD_DIRECTORY)
                        if (path != null) {
                            dir = path.absolutePathString()
                        }
                    }
                }
            ) {
                Text("...")
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun export(
        size: Size,
        states: List<FixedPresentationState>,
        progress: (ExportStatus.InProgress) -> Unit,
    ) = withContext(Dispatchers.IO) {
    val dirPath = Path(dir)
    dirPath.createDirectories()

    val sceneWidth = (size.widthIn * size.density).roundToInt()
    val sceneHeight = (size.heightIn * size.density).roundToInt()

    states.forEachIndexed { index, state ->
        progress(ExportStatus.InProgress("${state.currentSlide.name} - ${state.currentPosition.step}", index.toFloat() / (states.size + 1).toFloat()))
        val png = renderSceneToPng(sceneWidth, sceneHeight) { FixedCupSlide(sceneWidth, sceneHeight, state) }

            dirPath.resolve("${state.currentPosition.slideIndex}-${state.currentSlide.name}-${state.currentPosition.step}.png").writeBytes(png)
        }
    }

    @Composable
    override fun ColumnScope.Done() {
        Button(
            onClick = {
                Desktop.getDesktop().open(File(dir))
            },
        ) {
            Text("OPEN DIRECTORY")
        }
    }

    override val isFormValid: Boolean get() = dir.isNotBlank()
}
