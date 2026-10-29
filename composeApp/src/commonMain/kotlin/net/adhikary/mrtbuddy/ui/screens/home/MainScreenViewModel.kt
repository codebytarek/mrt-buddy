package net.adhikary.mrtbuddy.ui.screens.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.model.CardReadResult
import net.adhikary.mrtbuddy.model.Transaction
import net.adhikary.mrtbuddy.model.TransactionWithAmount
import net.adhikary.mrtbuddy.repository.TransactionRepository
import kotlinx.coroutines.flow.collect
import net.adhikary.mrtbuddy.changeLang
import net.adhikary.mrtbuddy.repository.SettingsRepository

class MainScreenViewModel(
    private val transactionRepository: TransactionRepository,
    private val initialState: MainScreenState,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    companion object {
        const val CARD_IDENTIFIER_ERROR = "CARD_IDENTIFIER_ERROR"
        const val SAVE_HISTORY_ERROR = "SAVE_HISTORY_ERROR"
    }

    private var autoSaveEnabled: Boolean = true

    private val _state: MutableStateFlow<MainScreenState> = MutableStateFlow(initialState)

    init {
        viewModelScope.launch {
            settingsRepository.autoSaveEnabled.collect { isEnabled ->
                autoSaveEnabled = isEnabled
            }
        }
        viewModelScope.launch {
            settingsRepository.currentLanguage.collect { language ->
                _state.update { it.copy(currentLanguage = language) }
            }
        }
        viewModelScope.launch {
            settingsRepository.darkThemeConfig.collect { config ->
                _state.update { it.copy(darkThemeConfig = config) }
            }
        }
    }

    val state: StateFlow<MainScreenState> get() = _state.asStateFlow()

    private val _events: Channel<MainScreenEvent> = Channel(Channel.BUFFERED)
    val events: Flow<MainScreenEvent> get() = _events.receiveAsFlow()

    fun onAction(action: MainScreenAction) {
        when (action) {


            is MainScreenAction.OnInit -> {
                viewModelScope.launch {
                    val savedLanguage = settingsRepository.currentLanguage.value
                    changeLang(savedLanguage)
                    _state.update { it.copy(currentLanguage = savedLanguage) }
                }
            }

            is MainScreenAction.UpdateCardState -> {
                _state.update {
                    when (action.newState) {
                        CardState.WaitingForTap, CardState.Reading -> it.withoutCurrentScanResult(action.newState)
                        else -> it.copy(cardState = action.newState)
                    }
                }
            }

            is MainScreenAction.UpdateCardReadResult -> {
                val result = action.cardReadResult
                if (result.idm.isBlank()) {
                    _state.update { it.copy(error = CARD_IDENTIFIER_ERROR) }
                    return
                }
                val mappedTransactions = mapTransactionsWithAmount(result.transactions)
                _state.update {
                    it.withScanResult(result, mappedTransactions)
                }

                viewModelScope.launch {
                    try {
                        if (autoSaveEnabled) transactionRepository.saveCardReadResult(result)
                        val card = transactionRepository.getCardByIdm(result.idm)
                        _state.update { it.copy(cardName = card?.name) }
                    } catch (e: kotlinx.coroutines.CancellationException) {
                        throw e
                    } catch (_: Exception) {
                        _state.update { it.copy(error = SAVE_HISTORY_ERROR) }
                        _events.send(MainScreenEvent.Error(SAVE_HISTORY_ERROR))
                    }
                }
            }
        }
    }
}

internal fun mapTransactionsWithAmount(transactions: List<Transaction>): List<TransactionWithAmount> =
    transactions.mapIndexed { index, transaction ->
        val amount = transactions.getOrNull(index + 1)?.let { next ->
            transaction.balance - next.balance
        }
        TransactionWithAmount(transaction = transaction, amount = amount)
    }

internal fun MainScreenState.withScanResult(
    result: CardReadResult,
    transactionsWithAmount: List<TransactionWithAmount> = mapTransactionsWithAmount(result.transactions)
): MainScreenState = copy(
    cardIdm = result.idm,
    cardName = null,
    transaction = result.transactions,
    transactionWithAmount = transactionsWithAmount,
    hasCurrentScanResult = true,
    error = null
)

internal fun MainScreenState.withoutCurrentScanResult(newCardState: CardState): MainScreenState = copy(
    cardState = newCardState,
    cardIdm = null,
    cardName = null,
    transaction = emptyList(),
    transactionWithAmount = emptyList(),
    hasCurrentScanResult = false,
    error = null
)
