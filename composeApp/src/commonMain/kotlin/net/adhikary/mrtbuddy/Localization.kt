package net.adhikary.mrtbuddy

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.cardRemovedTooSoon
import mrtbuddy.composeapp.generated.resources.unknownScanError
import org.jetbrains.compose.resources.stringResource

val LocalLocalization = staticCompositionLocalOf { Language.English.isoFormat }

@Composable
expect fun PlatformLocalizedResources(language: String, content: @Composable () -> Unit)

@Composable
fun LocalizedApp(language: String = Language.English.isoFormat, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalLocalization provides language,
    ) {
        PlatformLocalizedResources(language, content)
    }
}

@Composable
fun localizedNfcErrorMessage(message: String): String = when (
    message.trim().trimEnd('.', '!', '?').lowercase()
) {
    "tag was lost" -> stringResource(Res.string.cardRemovedTooSoon)
    "unknown error occurred" -> stringResource(Res.string.unknownScanError)
    else -> message
}
