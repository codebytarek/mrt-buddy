package net.adhikary.mrtbuddy.ui.screens.more

import net.adhikary.mrtbuddy.repository.DarkThemeConfig

data class MoreScreenState(
    val isLoading: Boolean = false,
    val autoSaveEnabled: Boolean = false,
    val currentLanguage: String = "en",
    val error: String? = null,
    val darkThemeConfig: DarkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM
)
