package net.adhikary.mrtbuddy.ui.components

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.apps
import mrtbuddy.composeapp.generated.resources.calculate
import mrtbuddy.composeapp.generated.resources.card
import mrtbuddy.composeapp.generated.resources.history
import mrtbuddy.composeapp.generated.resources.more_menu
import mrtbuddy.composeapp.generated.resources.fare
import mrtbuddy.composeapp.generated.resources.balance
import mrtbuddy.composeapp.generated.resources.more
import mrtbuddy.composeapp.generated.resources.historyTab
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
public fun CalculatorIcon() {
    return Icon(
        painter = painterResource(Res.drawable.calculate),
        contentDescription = stringResource(Res.string.fare)
    )
}

@Composable
public fun CardIcon() {
    return Icon(
        painter = painterResource(Res.drawable.card),
        contentDescription = stringResource(Res.string.balance)
    )
}

@Composable
public fun AppsIcon() {
    return Icon(
        painter = painterResource(Res.drawable.more_menu),
        contentDescription = stringResource(Res.string.more)
    )
}

@Composable
public fun HistoryIcon() {
    return Icon(
        painter = painterResource(Res.drawable.history),
        contentDescription = stringResource(Res.string.historyTab)
    )
}
