import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import net.kodein.cup.Slide
import net.kodein.cup.widgets.material.BulletPoints
import org.kodein.emoji.Emoji
import org.kodein.emoji.smileys_emotion.emotion.Collision
import org.kodein.emoji.smileys_emotion.face_smiling.Wink
import utils.TextWithAnimatedEmoji
import utils.TextWithEmoji
import utils.Title
import utils.y3DRotation

val steps by Slide(
    stepCount = 5,
    specs = { copy(endTransitions = y3DRotation(LocalLayoutDirection.current)) },
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
        modifier = Modifier
    ) {
        Title {
            TextWithAnimatedEmoji("Or attracting attention! ${Emoji.Collision}")
        }
    }
}
