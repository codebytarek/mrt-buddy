package net.adhikary.mrtbuddy.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.adhikary.mrtbuddy.repository.TransactionRepository

class HistoryScreenViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    companion object {
        const val LOAD_ERROR = "history_load_failed"
        const val RENAME_ERROR = "history_rename_failed"
        const val DELETE_ERROR = "history_delete_failed"
    }

    private val _state = MutableStateFlow(HistoryScreenState())
    val state: StateFlow<HistoryScreenState> get() = _state.asStateFlow()

    private val _events: Channel<HistoryScreenEvent> = Channel(Channel.BUFFERED)
    val events: Flow<HistoryScreenEvent> get() = _events.receiveAsFlow()

    private var observeJob: Job? = null

    init {
        // Start observing persisted cards with the ViewModel, not only after the screen's
        // first composition. The Room flow remains active while the user changes tabs.
        observeCards()
    }

    fun onAction(action: HistoryScreenAction) {
        when (action) {
            HistoryScreenAction.OnInit,
            HistoryScreenAction.OnCardAdded -> observeCards(restart = true)

            is HistoryScreenAction.RenameCard -> viewModelScope.launch {
                try {
                    transactionRepository.renameCard(action.cardIdm, action.newName)
                    _events.send(HistoryScreenEvent.RenameSuccess)
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {
                    _events.send(HistoryScreenEvent.Error(RENAME_ERROR))
                }
            }

            is HistoryScreenAction.DeleteCard -> viewModelScope.launch {
                try {
                    transactionRepository.deleteCard(action.cardIdm)
                    _events.send(HistoryScreenEvent.DeleteSuccess)
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {
                    _events.send(HistoryScreenEvent.Error(DELETE_ERROR))
                }
            }
        }
    }

    private fun observeCards(restart: Boolean = false) {
        // Re-subscribing forces Room to run a fresh query after returning from system
        // settings or recreating the Activity for a configuration change. Keep the
        // current rows visible during that refresh instead of resetting to empty.
        if (!restart && observeJob?.isActive == true) return
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            transactionRepository.observeCardsWithLatestBalance()
                .onStart {
                    _state.update {
                        it.copy(
                            isLoading = it.cards.isEmpty(),
                            isRefreshing = it.cards.isNotEmpty(),
                            error = null
                        )
                    }
                }
                .catch { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = LOAD_ERROR
                        )
                    }
                    _events.send(HistoryScreenEvent.Error(LOAD_ERROR))
                }
                .collect { records ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            cards = records.map { record ->
                                CardWithBalance(record.card, record.balance)
                            },
                            error = null,
                            lastUpdated = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
                        )
                    }
                }
        }
    }
}
