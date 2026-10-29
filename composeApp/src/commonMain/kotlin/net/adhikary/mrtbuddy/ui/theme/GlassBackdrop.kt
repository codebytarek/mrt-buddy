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
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.luminance

/** Soft accent light behind translucent app surfaces; clipped to the app window. */
@Composable
fun GlassBackdrop() {
    val dark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val base = if (dark) Color(0xFF101D25) else Color(0xFFEEF8F7)
    val teal = if (dark) Color(0xFF176B78) else Color(0xFF70C7D0)
    val warm = if (dark) Color(0xFF9A5527) else Color(0xFFFFB263)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        base,
                        if (dark) Color(0xFF172832) else Color(0xFFF4F8F4),
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
                            teal.copy(alpha = if (dark) 0.24f else 0.32f),
                            teal.copy(alpha = 0.10f),
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
                            warm.copy(alpha = if (dark) 0.19f else 0.27f),
                            warm.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
                .blur(76.dp)
        )
    }
}
