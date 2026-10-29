package net.adhikary.mrtbuddy

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "android"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual val isDebug: Boolean = BuildConfig.DEBUG

@Composable
actual fun ConfigureSystemBars(darkTheme: Boolean) {
    val view = LocalView.current
    val context = LocalContext.current
    val background = MaterialTheme.colorScheme.background
    SideEffect {
        val window = (context as? Activity)?.window ?: return@SideEffect
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        if (Build.VERSION.SDK_INT >= 28) {
            // Avoid Android drawing a contrasting divider across the transparent glass bar.
            window.navigationBarDividerColor = android.graphics.Color.TRANSPARENT
        }
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = !darkTheme
            isAppearanceLightNavigationBars = !darkTheme
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
        }
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            window.isStatusBarContrastEnforced = false
            window.isNavigationBarContrastEnforced = false
        }
    }
}
