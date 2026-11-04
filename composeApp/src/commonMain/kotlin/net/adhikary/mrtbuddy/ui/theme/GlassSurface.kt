package net.adhikary.mrtbuddy.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy

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

/** Shared soft reflections used by the floating navigation and balance card. */
fun Modifier.navigationGlassHighlight(shape: Shape): Modifier = composed {
    val dark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    this
        .clip(shape)
        .drawBehind {
            drawRect(
                brush = Brush.verticalGradient(
                    colorStops = arrayOf(
                        0f to Color.White.copy(alpha = if (dark) 0.30f else 0.44f),
                        0.12f to Color.White.copy(alpha = if (dark) 0.12f else 0.20f),
                        0.42f to Color.Transparent,
                        0.78f to Color.Transparent,
                        1f to Color.Black.copy(alpha = if (dark) 0.15f else 0.035f)
                    )
                ),
                topLeft = Offset.Zero,
                size = Size(size.width, size.height)
            )
            drawRect(
                brush = Brush.horizontalGradient(
                    listOf(
                        Color.White.copy(alpha = if (dark) 0.10f else 0.22f),
                        Color.Transparent,
                        Color.Transparent,
                        Color.White.copy(alpha = if (dark) 0.07f else 0.16f)
                    )
                ),
                topLeft = Offset.Zero,
                size = Size(size.width, size.height)
            )
        }
}

/** Shared translucent glass styling for information surfaces and floating controls. */
fun Modifier.glassSurface(shape: Shape, bottomEdgeAlpha: Float? = null): Modifier = composed {
    val dark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val colors = MaterialTheme.colorScheme
    // Use Monet's tonal surfaces for the body and keep the reflection restrained.
    val topHighlight = Color.White.copy(alpha = if (dark) 0.035f else 0.08f)
    val middleTint = colors.surfaceContainerHigh.copy(alpha = if (dark) 0.58f else 0.64f)
    val lowerTint = colors.surfaceContainerLow.copy(alpha = if (dark) 0.48f else 0.56f)
    val edge = colors.outlineVariant.copy(alpha = if (dark) 0.58f else 0.48f)
    val edgeBottom = colors.outlineVariant.copy(alpha = bottomEdgeAlpha ?: if (dark) 0.32f else 0.28f)
    this
        .shadow(
            elevation = if (dark) 7f.dp else 5f.dp,
            shape = shape,
            ambientColor = Color.Black.copy(alpha = if (dark) 0.15f else 0.045f),
            spotColor = Color.Black.copy(alpha = if (dark) 0.17f else 0.055f)
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
                    0.72f to colors.outlineVariant.copy(alpha = if (dark) 0.36f else 0.30f),
                    1f to edgeBottom
                )
            ),
            shape = shape
        )
}

/** Near-opaque frosted balance display: blurs content behind it without showing readable rows through. */
fun Modifier.balanceGlassSurface(shape: Shape, backdrop: Backdrop): Modifier = composed {
    val dark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val colors = MaterialTheme.colorScheme
    val fill = Brush.verticalGradient(
        colorStops = if (dark) {
            arrayOf(
                0f to colors.surfaceContainerHigh.copy(alpha = 0.93f),
                0.10f to colors.surface.copy(alpha = 0.92f),
                0.56f to colors.surface.copy(alpha = 0.90f),
                1f to colors.surfaceContainerHigh.copy(alpha = 0.94f)
            )
        } else {
            arrayOf(
                0f to Color.White.copy(alpha = 0.95f),
                0.10f to colors.surfaceContainerLow.copy(alpha = 0.94f),
                0.56f to colors.surface.copy(alpha = 0.92f),
                1f to colors.surfaceContainerLow.copy(alpha = 0.95f)
            )
        }
    )
    this
        .drawBackdrop(
            backdrop = backdrop,
            shape = { shape },
            effects = {
                vibrancy()
                blur(16.dp.toPx())
                lens(12.dp.toPx(), 24.dp.toPx())
            },
            onDrawSurface = {}
        )
        .clip(shape)
        .background(fill, shape)
        .drawBehind {
            drawRect(
                brush = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = if (dark) 0.12f else 0.18f),
                        Color.White.copy(alpha = if (dark) 0.04f else 0.06f),
                        Color.Transparent,
                        Color.Transparent
                    )
                ),
                size = Size(size.width, size.height)
            )
        }
        .border(
            width = 1.dp,
            brush = Brush.verticalGradient(
                listOf(
                    Color.White.copy(alpha = if (dark) 0.36f else 0.72f),
                    colors.outlineVariant.copy(alpha = if (dark) 0.40f else 0.42f),
                    colors.outlineVariant.copy(alpha = if (dark) 0.34f else 0.42f)
                )
            ),
            shape = shape
        )
}

/** Opaque frosted-glass treatment for controls and dialogs whose contents must stay easy to read. */
fun Modifier.frostedGlassSurface(shape: Shape): Modifier = composed {
    val dark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val surface = MaterialTheme.colorScheme.surface
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val fill = Brush.verticalGradient(
        colorStops = if (dark) {
            arrayOf(
                0f to surfaceVariant.copy(alpha = 0.98f),
                0.12f to surface.copy(alpha = 0.98f),
                0.72f to surface.copy(alpha = 0.97f),
                1f to surfaceVariant.copy(alpha = 0.98f)
            )
        } else {
            arrayOf(
                0f to Color.White.copy(alpha = 0.99f),
                0.12f to surface.copy(alpha = 0.98f),
                0.72f to surface.copy(alpha = 0.97f),
                1f to surfaceVariant.copy(alpha = 0.98f)
            )
        }
    )

    this
        .shadow(
            elevation = 10.dp,
            shape = shape,
            ambientColor = Color.Black.copy(alpha = if (dark) 0.22f else 0.08f),
            spotColor = Color.Black.copy(alpha = if (dark) 0.24f else 0.10f)
        )
        .clip(shape)
        .background(fill, shape)
        .drawBehind {
            drawRect(
                brush = Brush.verticalGradient(
                    colorStops = arrayOf(
                        0f to Color.White.copy(alpha = if (dark) 0.14f else 0.48f),
                        0.16f to Color.White.copy(alpha = if (dark) 0.045f else 0.13f),
                        0.44f to Color.Transparent,
                        1f to Color.Transparent
                    )
                ),
                size = Size(size.width, size.height)
            )
        }
        .border(
            width = 1.dp,
            brush = Brush.verticalGradient(
                listOf(
                    Color.White.copy(alpha = if (dark) 0.54f else 0.90f),
                    Color.White.copy(alpha = if (dark) 0.20f else 0.48f),
                    surfaceVariant.copy(alpha = if (dark) 0.52f else 0.54f)
                )
            ),
            shape = shape
        )
}
