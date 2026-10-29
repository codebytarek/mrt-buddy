package net.adhikary.mrtbuddy.repository

enum class DarkThemeConfig {
    FOLLOW_SYSTEM,
    LIGHT,
    DARK;

    fun next(): DarkThemeConfig = when (this) {
        FOLLOW_SYSTEM -> LIGHT
        LIGHT -> DARK
        DARK -> FOLLOW_SYSTEM
    }
}
