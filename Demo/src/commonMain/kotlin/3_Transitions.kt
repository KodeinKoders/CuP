import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import net.kodein.cup.Slide
import net.kodein.cup.utils.dataMapOf
import utils.TextWithEmoji
import utils.Title
import utils.y3DRotation


val transitions by Slide(
    stepCount = 2,
    specs = { copy(startTransitions = y3DRotation(LocalLayoutDirection.current)) },
    user = dataMapOf(
        KodeinPresentationBackground(KodeinTheme.Color.BackgroundSpecial),
    )
) { step ->
    Title(Modifier.padding(16.dp)) {
        Text("There can also be complex slide transition animations !")
    }
    AnimatedVisibility(step >= 1) {
        TextWithEmoji("Have you noticed the background change? 🤔")
    }
}
