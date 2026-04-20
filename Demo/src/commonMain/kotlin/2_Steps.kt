import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.kodein.cup.Slide
import net.kodein.cup.SlideSpecs
import net.kodein.cup.automove.AutoMovePause
import net.kodein.cup.imgexp.Export
import net.kodein.cup.utils.slideContextOf
import net.kodein.cup.widgets.material3.BulletPoints
import org.kodein.emoji.Emoji
import org.kodein.emoji.smileys_emotion.emotion.Collision
import org.kodein.emoji.smileys_emotion.face_smiling.Wink
import utils.TextWithAnimatedEmoji
import utils.TextWithEmoji
import utils.Title
import utils.y3DRotation
import kotlin.time.Duration.Companion.seconds

val steps by Slide(
    stepCount = 5,
    specs = SlideSpecs(endTransitions = y3DRotation),
    context = slideContextOf(
        Export.ignore(1..3),
        AutoMovePause.onSteps(1..3) {  1.seconds }
    )
) { step ->
    Title {
        Text("A slide may contain multiple steps.")
    }

    BulletPoints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp)
            .padding(horizontal = 64.dp)
    ) {
        BulletPoint(step >= 1) { Text("This slide has 5 steps!") }
        BulletPoint(step >= 2) { Text("You can use states to animate a lot of things and make your presentation more engaging!") }
        BulletPoint(step >= 3) {
            TextWithEmoji("...such as progressively revealing a list ${Emoji.Wink}")
        }
    }

    AnimatedVisibility(
        visible = step >= 4,
        enter = scaleIn(spring(Spring.DampingRatioHighBouncy, Spring.StiffnessMediumLow)) + expandVertically(clip = false),
    ) {
        Title(
            modifier = Modifier
                .padding(top = 32.dp)
        ) {
            TextWithAnimatedEmoji("${Emoji.Collision} Or attracting attention! ${Emoji.Collision}")
        }
    }
}
