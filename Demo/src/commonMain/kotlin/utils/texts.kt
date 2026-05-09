package utils

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@Composable
fun Title(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
//    CompositionLocalProvider(
//        LocalTextStyle provides LocalTextStyle.current.copy(
//            fontSize = 22.sp,
//            fontWeight = FontWeight.Bold,
//            textAlign = TextAlign.Center
//        )
//    ) {
    ProvideTextStyle(
        MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        ),
    ) {
        Column(
            content = content,
            modifier = modifier
                .padding(top = 8.dp, bottom = 16.dp)
        )
    }
}
