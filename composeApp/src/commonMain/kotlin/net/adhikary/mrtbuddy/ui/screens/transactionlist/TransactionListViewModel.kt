package net.adhikary.mrtbuddy.ui.screens.transactionlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.adhikary.mrtbuddy.repository.TransactionRepository

class TransactionListViewModel(
    private val cardIdm: String,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    companion object {
        const val LOAD_ERROR = "transaction_history_load_failed"
    }

    private val _state = MutableStateFlow(TransactionListState())
    val state: StateFlow<TransactionListState> = _state.asStateFlow()
    private var observeJob: Job? = null

    init {
        refresh()
    }

    fun refresh() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            transactionRepository.observeTransactionHistory(cardIdm)
                .onStart {
                    _state.update { it.copy(isLoading = it.transactions.isEmpty(), error = null) }
                }
                .catch {
                    _state.update { it.copy(isLoading = false, error = LOAD_ERROR) }
                }
                .collect { history ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            transactions = history.transactions,
                            balance = history.balance,
                            cardName = history.cardName,
                            error = null
                        )
                    }
                }
        }
    }
}
