package net.adhikary.mrtbuddy

import java.text.DecimalFormat
import java.util.Locale
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration

actual fun changeLang(lang: String) {
    val locale = Locale.forLanguageTag(
        if (lang.substringBefore('-').equals("bn", ignoreCase = true)) "bn" else "en"
    )
    Locale.setDefault(locale)
}

@Composable
actual fun PlatformLocalizedResources(language: String, content: @Composable () -> Unit) {
    val currentConfiguration = LocalConfiguration.current
    val locale = remember(language) {
        Locale.forLanguageTag(
            if (language.substringBefore('-').equals("bn", ignoreCase = true)) "bn" else "en"
        )
    }
    val localizedConfiguration = remember(currentConfiguration, locale) {
        Configuration(currentConfiguration).apply {
            setLocale(locale)
            setLayoutDirection(locale)
        }
    }

    SideEffect { Locale.setDefault(locale) }
    CompositionLocalProvider(LocalConfiguration provides localizedConfiguration, content = content)
}

actual fun translateNumber(number: Int): String {
    return DecimalFormat.getInstance().format(number).replace(",", "")  // Remove commas
}
