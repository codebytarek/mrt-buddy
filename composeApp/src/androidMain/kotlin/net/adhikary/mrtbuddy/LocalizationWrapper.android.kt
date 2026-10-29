package net.adhikary.mrtbuddy

import java.text.DecimalFormat
import java.util.Locale
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

actual fun changeLang(lang: String) {
    val languageTag = when (lang.substringBefore('-').lowercase()) {
        "bn" -> "bn"
        else -> "en"
    }
    val locale = Locale.forLanguageTag(languageTag)
    Locale.setDefault(locale)
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageTag))
}

actual fun translateNumber(number: Int): String {
    return DecimalFormat.getInstance().format(number).replace(",", "")  // Remove commas
}
