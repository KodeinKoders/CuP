package net.kodein.cup.speaker

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikepenz.markdown.m2.Markdown
import kotlinx.coroutines.delay
import net.kodein.cup.LocalPresentationState
import net.kodein.cup.PresentationState
import net.kodein.cup.currentSlide
import net.kodein.cup.laser.Laser
import net.kodein.cup.utils.CupToolsColors
import net.kodein.cup.utils.CupToolsMaterialColors

@Composable
internal fun SWMainView(
    ratio: Float,
    laser: Laser?,
    setLaser: (Laser?) -> Unit,
) {
    val presentationState = LocalPresentationState.current

    val density = LocalDensity.current
    var windowHeight by remember { mutableStateOf(0.dp) }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { windowHeight = with(density) { it.height.toDp() } }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxHeight()
                .weight(2f)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .sizeIn(maxHeight = windowHeight - 180.dp)
            ) {
                SWCurrentSlideView(
                    ratio = ratio,
                    laser = laser,
                    setLaser = setLaser,
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                SWStepRow(presentationState)
                SWTimer()
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .sizeIn(maxHeight = windowHeight / 2)
            ) {
                SWNextSlideView(
                    presentationState = presentationState,
                    ratio = ratio,
                )
            }
            SWNotes(presentationState)
        }
    }
}

@Composable
private fun SWTimer() {

    var started by remember { mutableStateOf(false) }

    var elapsedSeconds by remember { mutableStateOf(0) }

    LaunchedEffect(started) {
        if (!started) return@LaunchedEffect
        val t0 = System.currentTimeMillis()
        var next = t0
        while (true) {
            next += 1_000
            delay(next - System.currentTimeMillis())
            elapsedSeconds += 1
        }
    }

    MaterialTheme(colors = CupToolsMaterialColors) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column {
                Button(
                    onClick = { started = !started },
                    modifier = Modifier.width(96.dp)
                ) { Text(if (started) "PAUSE" else "START") }
                Button(
                    onClick = { elapsedSeconds = 0 },
                    enabled = !started,
                    modifier = Modifier.width(96.dp)
                ) { Text("RESET") }
            }
            val seconds = elapsedSeconds % 60
            val minutes = (elapsedSeconds - seconds) / 60
            CompositionLocalProvider(
                LocalTextStyle provides LocalTextStyle.current.copy(fontSize = 48.sp),
                LocalLayoutDirection provides LayoutDirection.Ltr
            ) {
                Row {
                    Text(
                        text = minutes.toString().padStart(2, '0'),
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(128.dp)
                    )
                    Text(
                        text = " : ",
                        modifier = Modifier.alpha(0.5f)
                    )
                    Text(
                        text = seconds.toString().padStart(2, '0'),
                        fontWeight = FontWeight.Light,
                        modifier = Modifier.width(96.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SWStepRow(presentationState: PresentationState) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(horizontal = 16.dp).height(16.dp)
    ) {
        repeat(presentationState.currentSlide.stepCount - 1) {
            Spacer(
                Modifier
                    .height(16.dp)
                    .weight(1f)
                    .border(4.dp, CupToolsColors.dark.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .then(
                        if (presentationState.currentStep > it) Modifier.background(CupToolsColors.dark.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        else Modifier
                    )
            )
        }
    }
}

@Composable
private fun SWNotes(presentationState: PresentationState) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            MaterialTheme(colors = CupToolsMaterialColors) {
                CompositionLocalProvider(
                    LocalTextStyle provides LocalTextStyle.current.copy(fontSize = 18.sp)
                ) {
                    val speakerNotes = presentationState.currentSlide.user[SpeakerNotes]
                    if (speakerNotes != null) {
                        val (_, notes) = speakerNotes.notes.first { (range, _) -> presentationState.currentStep in range }
                        Markdown(notes.trimIndent())
                    }
                }
            }
        }
        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(scrollState),
            modifier = Modifier.fillMaxHeight()
        )
    }
}
