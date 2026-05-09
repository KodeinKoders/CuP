package net.kodein.cup.imgexp.utils

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.skiko.MainUIDispatcher
import java.awt.FileDialog
import java.awt.Frame
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.isDirectory


private const val appleDirectoryDialogProperty = "apple.awt.fileDialogForDirectories"

internal enum class FileDialogMode {
    LOAD_FILE,
    LOAD_DIRECTORY,
    SAVE_FILE,
}

internal suspend fun fileDialog(
    title: String,
    mode: FileDialogMode,
    filter: (Path) -> Boolean = { true },
): Path? {
    return coroutineScope {
        val def = CompletableDeferred<Path?>()
        val dialog = object : FileDialog(null as Frame?, title, LOAD) {
            @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
            override fun hide() {
                super.hide()
                if (!def.isCompleted) {
                    val selected = file?.let { Path(this.directory, this.file) }
                    launch { def.complete(selected) }
                }
            }
        }
        dialog.mode = when (mode) {
            FileDialogMode.LOAD_FILE, FileDialogMode.LOAD_DIRECTORY -> FileDialog.LOAD
            FileDialogMode.SAVE_FILE -> FileDialog.SAVE
        }
        dialog.setFilenameFilter { dir, name ->
            val path = Path(dir.absolutePath, name)
            if (mode == FileDialogMode.LOAD_DIRECTORY && !path.isDirectory()) false
            else filter(path)
        }
        dialog.isModal = false

        val prev = System.setProperty(
            appleDirectoryDialogProperty,
            when (mode) {
                FileDialogMode.LOAD_DIRECTORY -> "true"
                FileDialogMode.LOAD_FILE, FileDialogMode.SAVE_FILE -> "false"
            }
        )

        withContext(MainUIDispatcher) {
            dialog.isVisible = true
        }

        try {
            def.await()
        } finally {
            dialog.dispose()
            if (prev != null) System.setProperty(appleDirectoryDialogProperty, prev)
            else System.clearProperty(appleDirectoryDialogProperty)
        }
    }
}
