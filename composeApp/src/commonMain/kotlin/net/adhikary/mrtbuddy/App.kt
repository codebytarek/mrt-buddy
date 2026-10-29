package net.adhikary.mrtbuddy

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.adhikary.mrtbuddy.managers.RescanManager
import net.adhikary.mrtbuddy.nfc.getNFCManager
import net.adhikary.mrtbuddy.ui.screens.home.MainScreen
import net.adhikary.mrtbuddy.ui.screens.home.MainScreenAction
import net.adhikary.mrtbuddy.ui.screens.home.MainScreenEvent
import net.adhikary.mrtbuddy.ui.screens.home.MainScreenState
import net.adhikary.mrtbuddy.ui.screens.home.MainScreenViewModel
import net.adhikary.mrtbuddy.repository.DarkThemeConfig
import net.adhikary.mrtbuddy.ui.theme.MRTBuddyTheme
import net.adhikary.mrtbuddy.ui.theme.GlassBackdrop
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import net.adhikary.mrtbuddy.utils.observeAsActions
import net.adhikary.mrtbuddy.ConfigureSystemBars
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean
) {
    val mainVm = koinViewModel<MainScreenViewModel>()
    val nfcManager = getNFCManager()


    mainVm.events.observeAsActions { event ->
        when (event) {
            is MainScreenEvent.Error -> {}
            MainScreenEvent.ShowMessage -> {}

        }
    }


    if (RescanManager.isRescanRequested.value) {
        RescanManager.resetRescanRequest()
    }

    LaunchedEffect(nfcManager, mainVm) {
        coroutineScope {
            launch {
                nfcManager.cardReadResults.collectLatest { result ->
                    result?.let { mainVm.onAction(MainScreenAction.UpdateCardReadResult(it)) }
                }
            }
            launch {
                nfcManager.cardState.collectLatest {
                    mainVm.onAction(MainScreenAction.UpdateCardState(it))
                }
            }
        }
    }


    nfcManager.startScan()

    val state: MainScreenState by mainVm.state.collectAsState()
    val useDarkTheme = when (state.darkThemeConfig) {
        DarkThemeConfig.FOLLOW_SYSTEM -> darkTheme
        DarkThemeConfig.LIGHT -> false
        DarkThemeConfig.DARK -> true
    }
    MRTBuddyTheme(
        darkTheme = useDarkTheme,
        dynamicColor = dynamicColor
    ) {
        ConfigureSystemBars(useDarkTheme)
        LocalizedApp(
            language = state.currentLanguage
        ) {
            Box(Modifier.fillMaxSize()) {
                GlassBackdrop()
                MainScreen()
            }
        }
    }
}
