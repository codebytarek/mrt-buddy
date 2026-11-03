package net.adhikary.mrtbuddy.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme

/** Soft accent light behind translucent app surfaces; clipped to the app window. */
@Composable
fun GlassBackdrop() {
    val colors = MaterialTheme.colorScheme
    val dark = colors.surface.luminance() < 0.5f
    val base = colors.background
    val middle = colors.surface
    val primaryGlow = colors.primary
    val tertiaryGlow = colors.tertiary
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        base,
                        middle,
                        base
                    )
                )
            )
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-130).dp, y = (-100).dp)
                .size(390.dp)
                .background(
                    Brush.radialGradient(
                        listOf(
                            primaryGlow.copy(alpha = if (dark) 0.13f else 0.18f),
                            primaryGlow.copy(alpha = if (dark) 0.05f else 0.07f),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
                .blur(70.dp)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 130.dp, y = 120.dp)
                .size(420.dp)
                .background(
                    Brush.radialGradient(
                        listOf(
                            tertiaryGlow.copy(alpha = if (dark) 0.12f else 0.16f),
                            tertiaryGlow.copy(alpha = if (dark) 0.04f else 0.06f),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
                .blur(76.dp)
        )
    }
}
