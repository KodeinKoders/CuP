package utils

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp


@Composable
fun InlineIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    width: TextUnit = 20.sp,
    height: TextUnit = 20.sp,
    tint: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
) =
    InlineTextContent(Placeholder(width, height, PlaceholderVerticalAlign.TextCenter)) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = modifier.fillMaxSize(),
            tint = tint
        )
    }