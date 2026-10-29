package net.adhikary.mrtbuddy.ui.components

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.datetime.LocalDateTime
import net.adhikary.mrtbuddy.model.CardReadResult
import net.adhikary.mrtbuddy.model.Transaction
import net.adhikary.mrtbuddy.ui.screens.home.MainScreenState
import net.adhikary.mrtbuddy.ui.screens.home.withScanResult
import net.adhikary.mrtbuddy.ui.screens.home.withoutCurrentScanResult
import net.adhikary.mrtbuddy.ui.screens.home.Screen
import net.adhikary.mrtbuddy.model.TransactionType

class MainScreenTest {
    private class NavigationState(var currentScreen: Screen = Screen.Home)

    @Test
    fun navigationStateCanMoveBetweenExistingDestinations() {
        val state = NavigationState()

        assertEquals(Screen.Home, state.currentScreen)
        state.currentScreen = Screen.Calculator
        assertEquals(Screen.Calculator, state.currentScreen)
        state.currentScreen = Screen.Home
        assertEquals(Screen.Home, state.currentScreen)
    }

    @Test
    fun successfulScanPopulatesVisibleJourneyStateAndKeepsUnknownFareUnknown() {
        val newest = transaction(day = 2, balance = 960)
        val oldest = transaction(day = 1, balance = 1000)
        val state = MainScreenState().withScanResult(
            CardReadResult("04 01 02 03", listOf(newest, oldest))
        )

        assertTrue(state.hasCurrentScanResult)
        assertEquals("04 01 02 03", state.cardIdm)
        assertEquals(listOf(newest, oldest), state.transaction)
        assertEquals(-40, state.transactionWithAmount[0].amount)
        assertEquals(null, state.transactionWithAmount[1].amount)
    }

    @Test
    fun recentActivityIsHiddenUntilTheCurrentCardScanHasFinished() {
        val scanned = MainScreenState().withScanResult(
            CardReadResult("card-id", listOf(transaction(day = 2, balance = 960)))
        )

        assertTrue(!scanned.hasVisibleCurrentScanHistory)
        val balanceVisible = scanned.copy(cardState = net.adhikary.mrtbuddy.model.CardState.Balance(960))
        assertTrue(balanceVisible.hasVisibleCurrentScanHistory)

        val waitingForNextTap = balanceVisible.withoutCurrentScanResult(
            net.adhikary.mrtbuddy.model.CardState.WaitingForTap
        )
        assertTrue(!waitingForNextTap.hasVisibleCurrentScanHistory)
        assertTrue(waitingForNextTap.transaction.isEmpty())
        assertTrue(waitingForNextTap.transactionWithAmount.isEmpty())
    }

    @Test
    fun transactionKindComesFromTheCardFixedHeader() {
        assertEquals(
            TransactionType.BalanceUpdate,
            TransactionType.fromHeader("1D 60 02 01")
        )
        assertEquals(
            TransactionType.CommuteDhakaMetro,
            TransactionType.fromHeader("08 52 10 00")
        )
        assertEquals(
            TransactionType.CommuteUnknown,
            TransactionType.fromHeader("00 00 00 00")
        )
    }

    private fun transaction(day: Int, balance: Int) = Transaction(
        fixedHeader = "01 02 03 04",
        timestamp = LocalDateTime(2019, 7, day, 10, 30),
        transactionType = "00 01",
        fromStation = "Farmgate",
        toStation = "Karwan Bazar",
        balance = balance,
        trailing = "00 00"
    )
}
