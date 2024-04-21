package net.kodein.cup.desktop

import androidx.compose.runtime.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.kodein.cup.LocalPresentationState
import net.kodein.cup.PresentationState
import net.kodein.cup.currentSlide
import net.kodein.cup.withPresentationState
import java.io.IOException
import java.util.*
import kotlin.io.path.*


@Composable
public fun withCupSavedPresentationState(
    content: @Composable (PresentationState?) -> Unit
) {
    var restored: Pair<String, Int>? by remember { mutableStateOf(null) }

    LaunchedEffect(null) {
        val props = withContext(Dispatchers.IO) {
            val windowProps = Path(".cup", "state.properties")
            if (windowProps.exists()) {
                windowProps.reader().use { reader ->
                    Properties().also { it.load(reader) }
                }
            } else null
        }
        if (props != null && props.containsKey("slide") && props.containsKey("step")) {
            restored = props.getProperty("slide") to (props.getProperty("step").toIntOrNull() ?: 0)
        } else {
            restored = "" to 0
        }
    }

    if (restored == null) {
        content(null)
    }
    else {
        val (initialName, initialStep) = restored!!
        withPresentationState(
            initial = { slides -> slides.indexOfFirst { it.name == initialName } to initialStep }
        ) {
            val presentationState = LocalPresentationState.current
            LaunchedEffect(presentationState.slides) {
                if (presentationState.slides.isEmpty()) return@LaunchedEffect
                try {
                    withContext(Dispatchers.IO) {
                        Path(".cup").createDirectories()
                        snapshotFlow { presentationState.currentSlide.name to presentationState.currentStep }
                            .collect { (slideName, step) ->
                                Path(".cup", "state.properties").writer().use { writer ->
                                    Properties().also {
                                        it.setProperty("slide", slideName)
                                        it.setProperty("step", step.toString())
                                    }.store(writer, null)
                                }
                            }
                    }
                } catch (_: IOException) {}
            }
            content(presentationState)
        }
    }
}
