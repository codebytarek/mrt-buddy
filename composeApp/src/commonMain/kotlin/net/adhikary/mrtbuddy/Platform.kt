package net.adhikary.mrtbuddy

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect val isDebug: Boolean

@androidx.compose.runtime.Composable
expect fun ConfigureSystemBars(darkTheme: Boolean)
