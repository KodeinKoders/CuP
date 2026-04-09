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

internal suspend fun directoryDialog(
    title: String,
): Path? {
    return coroutineScope {
        val def = CompletableDeferred<Path?>()
        val dialog = object : FileDialog(null as Frame?, title, LOAD) {
            @Suppress("DEPRECATION")
            @Deprecated("Deprecated in Java")
            override fun hide() {
                super.hide()
                if (!def.isCompleted) {
                    val selected = file?.let { Path(directory, file) }
                    launch { def.complete(selected) }
                }
            }
        }
        dialog.setFilenameFilter { dir, name -> Path(dir.absolutePath, name).isDirectory() }
        dialog.isModal = false

        val prev = System.setProperty(appleDirectoryDialogProperty, "true")

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
