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

/** Higher-contrast, layered glass treatment for the floating bottom navigation. */
fun Modifier.navigationGlassSurface(shape: Shape): Modifier = composed {
    val dark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val base = MaterialTheme.colorScheme.surface
    val fill = if (dark) {
        Brush.verticalGradient(
            colorStops = arrayOf(
                0f to Color.White.copy(alpha = 0.24f),
                0.08f to base.copy(alpha = 0.84f),
                0.48f to base.copy(alpha = 0.76f),
                1f to base.copy(alpha = 0.90f)
            )
        )
    } else {
        Brush.verticalGradient(
            colorStops = arrayOf(
                0f to Color.White.copy(alpha = 0.80f),
                0.10f to Color.White.copy(alpha = 0.62f),
                0.56f to Color.White.copy(alpha = 0.52f),
                1f to Color.White.copy(alpha = 0.68f)
            )
        )
    }
    this
        .shadow(
            elevation = 15.dp,
            shape = shape,
            ambientColor = Color.Black.copy(alpha = if (dark) 0.24f else 0.09f),
            spotColor = Color.Black.copy(alpha = if (dark) 0.28f else 0.12f)
        )
        .clip(shape)
        .background(fill, shape)
        .border(
            width = 1.dp,
            brush = Brush.verticalGradient(
                listOf(
                    Color.White.copy(alpha = if (dark) 0.76f else 0.94f),
                    Color.White.copy(alpha = if (dark) 0.38f else 0.54f),
                    Color.White.copy(alpha = if (dark) 0.22f else 0.42f)
                )
            ),
            shape = shape
        )
}

/** Shared translucent glass styling for information surfaces and floating controls. */
fun Modifier.glassSurface(shape: Shape, bottomEdgeAlpha: Float? = null): Modifier = composed {
    val dark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    // A strong white wash reads as a flat rectangle in light mode instead of glass.
    val topHighlight = Color.White.copy(alpha = if (dark) 0.22f else 0.14f)
    val middleTint = MaterialTheme.colorScheme.surface.copy(alpha = if (dark) 0.13f else 0.08f)
    val lowerTint = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (dark) 0.20f else 0.07f)
    val edge = Color.White.copy(alpha = if (dark) 0.36f else 0.32f)
    val edgeBottom = Color.White.copy(alpha = bottomEdgeAlpha ?: if (dark) 0.08f else 0.06f)
    this
        .shadow(
            elevation = if (dark) 10f.dp else 6f.dp,
            shape = shape,
            ambientColor = Color.Black.copy(alpha = if (dark) 0.20f else 0.07f),
            spotColor = Color.Black.copy(alpha = if (dark) 0.24f else 0.06f)
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
