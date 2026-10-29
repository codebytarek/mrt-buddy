package net.adhikary.mrtbuddy.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.Shape
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp

/** Shared translucent glass styling for information surfaces and floating controls. */
fun Modifier.glassSurface(shape: Shape, bottomEdgeAlpha: Float? = null): Modifier = composed {
    val dark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val topHighlight = Color.White.copy(alpha = if (dark) 0.30f else 0.42f)
    val middleTint = MaterialTheme.colorScheme.surface.copy(alpha = if (dark) 0.15f else 0.18f)
    val lowerTint = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (dark) 0.24f else 0.14f)
    val edge = Color.White.copy(alpha = if (dark) 0.50f else 0.48f)
    val edgeBottom = Color.White.copy(alpha = bottomEdgeAlpha ?: if (dark) 0.10f else 0.24f)
    this
        .shadow(
            elevation = if (dark) 12f.dp else 8f.dp,
            shape = shape,
            ambientColor = Color.Black.copy(alpha = if (dark) 0.24f else 0.10f),
            spotColor = Color.Black.copy(alpha = if (dark) 0.30f else 0.08f)
        )
        .clip(shape)
        .background(
            Brush.verticalGradient(listOf(topHighlight, middleTint, lowerTint)),
            shape
        )
        .border(
            width = if (dark) 0.8f.dp else 0.7f.dp,
            brush = Brush.verticalGradient(
                colorStops = arrayOf(
                    0f to edge,
                    0.72f to Color.White.copy(alpha = if (dark) 0.20f else 0.28f),
                    1f to edgeBottom
                )
            ),
            shape = shape
        )
}
