package net.kodein.cup.imgexp

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.geometry.Size
import net.kodein.cup.FixedPresentationState
import net.kodein.cup.LocalPresentationSize
import net.kodein.cup.LocalPresentationState
import net.kodein.cup.PresentationMainView
import net.kodein.cup.ProvideSlideContents


internal interface Exporter {
    val format: String
    @Composable
    fun ColumnScope.Form()
    suspend fun export(
        size: Size,
        states: List<FixedPresentationState>,
        progress: (ExportStatus.InProgress) -> Unit,
    )
    @Composable
    fun ColumnScope.Done()
    val isFormValid: Boolean

    data class Size(
        val widthIn: Float,
        val heightIn: Float,
        val density: Int,
    )
}

@Composable
internal fun FixedCupSlide(
    width: Int,
    height: Int,
    state: FixedPresentationState,
) {
    ProvideSlideContents(state) {
        CompositionLocalProvider(
            LocalPresentationSize provides Size(width.toFloat(), height.toFloat()),
            LocalPresentationState provides state,
        ) {
            PresentationMainView()
        }
    }
}