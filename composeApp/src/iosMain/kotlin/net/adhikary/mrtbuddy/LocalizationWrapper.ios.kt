package net.adhikary.mrtbuddy

import platform.Foundation.NSNumber
import platform.Foundation.NSUserDefaults
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterDecimalStyle
import androidx.compose.runtime.Composable

actual fun changeLang(
    lang: String
) {
    NSUserDefaults.standardUserDefaults.setObject(arrayListOf(lang),"AppleLanguages")
}

@Composable
actual fun PlatformLocalizedResources(language: String, content: @Composable () -> Unit) {
    content()
}

actual fun translateNumber(
    number: Int
): String {
    val numberFormatter = NSNumberFormatter()
    numberFormatter.numberStyle = NSNumberFormatterDecimalStyle
    return (numberFormatter.stringFromNumber(NSNumber(number)) ?: "").replace(",", "")
}
