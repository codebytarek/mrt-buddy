package net.adhikary.mrtbuddy.ui.viewmodel

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.BeforeTest
import kotlin.test.AfterTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.ui.screens.farecalculator.FareCalculatorAction
import net.adhikary.mrtbuddy.ui.screens.farecalculator.FareCalculatorViewModel

class FareCalculatorViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUpMainDispatcher() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDownMainDispatcher() {
        Dispatchers.resetMain()
    }

    @Test
    fun startsWithEmptyRouteAndZeroFare() {
        val viewModel = FareCalculatorViewModel()
        val state = viewModel.state.value

        assertEquals(null, state.fromStation)
        assertEquals(null, state.toStation)
        assertEquals(0, state.calculatedFare)
        assertEquals(0, state.discountedFare)
    }

    @Test
    fun dropdownActionsAreMutuallyExclusiveAndDismissible() {
        val viewModel = FareCalculatorViewModel()

        viewModel.onAction(FareCalculatorAction.ToggleFromExpanded)
        assertEquals(true, viewModel.state.value.fromExpanded)

        viewModel.onAction(FareCalculatorAction.ToggleToExpanded)
        assertEquals(false, viewModel.state.value.fromExpanded)
        assertEquals(true, viewModel.state.value.toExpanded)

        viewModel.onAction(FareCalculatorAction.DismissDropdowns)
        assertEquals(false, viewModel.state.value.fromExpanded)
        assertEquals(false, viewModel.state.value.toExpanded)
    }

    @Test
    fun cardStateCanBeUpdated() {
        val viewModel = FareCalculatorViewModel()
        val state = CardState.Balance(1000)

        viewModel.onAction(FareCalculatorAction.UpdateCardState(state))

        assertEquals(state, viewModel.state.value.cardState)
    }
}
