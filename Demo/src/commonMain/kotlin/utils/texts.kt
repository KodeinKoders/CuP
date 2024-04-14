package utils

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.kodein.emoji.compose.WithNotoAnimatedEmoji
import org.kodein.emoji.compose.WithPlatformEmoji


@Composable
fun Title(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    CompositionLocalProvider(
        LocalTextStyle provides LocalTextStyle.current.copy(
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    ) {
        Column(
            content = content,
            modifier = modifier
                .padding(top = 8.dp, bottom = 16.dp)
        )
    }
}

@Composable
fun TextWithEmoji(text: String) {
    WithPlatformEmoji(text) { aStr, ic -> Text(text = aStr, inlineContent = ic) }
}

@Composable
fun TextWithAnimatedEmoji(text: String) {
    WithNotoAnimatedEmoji(text) { aStr, ic -> Text(text = aStr, inlineContent = ic) }
}
