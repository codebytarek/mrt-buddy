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

/** A shared frosted-glass treatment for app surfaces (without blurring their readable content). */
fun Modifier.glassSurface(shape: Shape): Modifier = composed {
    val dark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    // Keep light-mode glass translucent so the ambient backdrop remains visible instead of
    // turning every surface into a white card with a hard outline.
    val topHighlight = Color.White.copy(alpha = if (dark) 0.25f else 0.46f)
    // Keep the backdrop visible through dark surfaces; a strong surface tint reads as a solid card.
    val middleTint = MaterialTheme.colorScheme.surface.copy(alpha = if (dark) 0.24f else 0.38f)
    val lowerTint = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (dark) 0.30f else 0.24f)
    val edge = Color.White.copy(alpha = if (dark) 0.48f else 0.30f)
    this
        .shadow(
            elevation = if (dark) 14f.dp else 7f.dp,
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
            width = if (dark) 0.8f.dp else 0.5f.dp,
            brush = Brush.verticalGradient(listOf(edge, Color.White.copy(alpha = if (dark) 0.10f else 0.10f))),
            shape = shape
        )
}
