package net.adhikary.mrtbuddy.repository

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.async
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDateTime
import net.adhikary.mrtbuddy.dao.CardDao
import net.adhikary.mrtbuddy.dao.ScanDao
import net.adhikary.mrtbuddy.dao.TransactionDao
import net.adhikary.mrtbuddy.data.CardEntity
import net.adhikary.mrtbuddy.data.CardEntityWithBalance
import net.adhikary.mrtbuddy.data.ScanEntity
import net.adhikary.mrtbuddy.data.TransactionEntity
import net.adhikary.mrtbuddy.model.CardReadResult
import net.adhikary.mrtbuddy.model.Transaction
import net.adhikary.mrtbuddy.ui.screens.history.HistoryScreenAction
import net.adhikary.mrtbuddy.ui.screens.history.HistoryScreenViewModel

class TransactionRepositoryTest {
    @Test
    fun persistsScannedTransactionsAndReturnsRecordWithoutComparableFare() = runTest {
        val cardDao = InMemoryCardDao()
        val scanDao = InMemoryScanDao()
        val transactionDao = InMemoryTransactionDao()
        val repository = TransactionRepository(cardDao, scanDao, transactionDao)
        val newest = transaction(day = 2, balance = 960)
        val oldest = transaction(day = 1, balance = 1000)

        repository.saveCardReadResult(CardReadResult("card-id", listOf(newest, oldest)))

        assertEquals("card-id", cardDao.getCardByIdm("card-id")?.idm)
        assertEquals(1, scanDao.getScansByCardIdm("card-id").size)
        val saved = repository.getTransactionsByCardIdm("card-id")
        assertEquals(2, saved.size)
        assertEquals(-40, saved[0].amount)
        assertNull(saved[1].amount)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun observedHistoryUpdatesWhenScannedTransactionsArePersisted() = runTest {
        val cardDao = InMemoryCardDao()
        val transactionDao = InMemoryTransactionDao()
        val repository = TransactionRepository(cardDao, InMemoryScanDao(), transactionDao)
        val observedCards = async {
            repository.observeCardsWithLatestBalance().first { it.isNotEmpty() }
        }
        val observedHistory = async {
            repository.observeTransactionHistory("card-id").first { it.transactions.size == 2 }
        }
        runCurrent()

        repository.saveCardReadResult(
            CardReadResult("card-id", listOf(transaction(day = 2, balance = 960), transaction(day = 1, balance = 1000)))
        )
        runCurrent()

        assertEquals("card-id", observedCards.await().single().card.idm)
        val history = observedHistory.await()
        assertEquals("card-id", history.transactions.first().transactionEntity.cardIdm)
        assertEquals(960, history.balance)
        assertEquals(-40, history.transactions.first().amount)
        assertTrue(history.transactions.all { it.transactionEntity.cardIdm == "card-id" })
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun historyObservationFailureIsNotPresentedAsEmptyHistory() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val repository = TransactionRepository(
                FailingCardDao(),
                InMemoryScanDao(),
                InMemoryTransactionDao()
            )
            val viewModel = HistoryScreenViewModel(repository)

            viewModel.onAction(HistoryScreenAction.OnInit)
            advanceUntilIdle()

            assertEquals(HistoryScreenViewModel.LOAD_ERROR, viewModel.state.value.error)
            assertTrue(viewModel.state.value.cards.isEmpty())
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun historyViewModelShowsCardsPersistedWhileHistoryIsOpen() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val cardDao = InMemoryCardDao()
            val repository = TransactionRepository(cardDao, InMemoryScanDao(), InMemoryTransactionDao())
            val viewModel = HistoryScreenViewModel(repository)
            viewModel.onAction(HistoryScreenAction.OnInit)
            runCurrent()

            repository.saveCardReadResult(
                CardReadResult("new-card", listOf(transaction(day = 2, balance = 860)))
            )
            advanceUntilIdle()

            assertEquals("new-card", viewModel.state.value.cards.single().card.idm)
            assertEquals(null, viewModel.state.value.cards.single().balance)
            assertEquals(null, viewModel.state.value.error)
        } finally {
            Dispatchers.resetMain()
        }
    }

    private fun transaction(day: Int, balance: Int) = Transaction(
        fixedHeader = "01 02 03 04",
        timestamp = LocalDateTime(2024, 7, day, 10, 30),
        transactionType = "00 01",
        fromStation = "Farmgate",
        toStation = "Karwan Bazar",
        balance = balance,
        trailing = "00 00"
    )

    private class InMemoryCardDao : CardDao {
        private val cards = linkedMapOf<String, CardEntity>()
        private val cardState = MutableStateFlow<List<CardEntity>>(emptyList())
        private fun publish() { cardState.value = cards.values.toList() }
        override suspend fun insertCard(card: CardEntity) {
            cards.putIfAbsent(card.idm, card)
            publish()
        }
        override suspend fun updateLastScanTime(cardIdm: String, scanTime: Long) {
            cards[cardIdm]?.let { cards[cardIdm] = it.copy(lastScanTime = scanTime) }
            publish()
        }
        override suspend fun getCardByIdm(idm: String): CardEntity? = cards[idm]
        override fun observeCardByIdm(idm: String) = cardState.map { list -> list.firstOrNull { it.idm == idm } }
        override suspend fun getAllCards(): List<CardEntity> = cards.values.toList()
        override fun observeCardsWithLatestBalance() = cardState.map { list ->
            list.map { CardEntityWithBalance(card = it, balance = null) }
        }
        override suspend fun updateCardName(cardIdm: String, newName: String) {
            cards[cardIdm]?.let { cards[cardIdm] = it.copy(name = newName) }
            publish()
        }
        override suspend fun deleteCard(cardIdm: String) {
            cards.remove(cardIdm)
            publish()
        }
    }

    private class FailingCardDao : CardDao by InMemoryCardDao() {
        override fun observeCardsWithLatestBalance(): Flow<List<CardEntityWithBalance>> = flow {
            throw IllegalStateException("storage unavailable")
        }
    }

    private class InMemoryScanDao : ScanDao {
        private val scans = mutableListOf<ScanEntity>()
        private var nextId = 1L
        override suspend fun insertScan(scan: ScanEntity): Long {
            val id = nextId++
            scans += scan.copy(scanId = id)
            return id
        }
        override suspend fun getScansByCardIdm(cardIdm: String): List<ScanEntity> =
            scans.filter { it.cardIdm == cardIdm }
        override suspend fun deleteScansByCardIdm(cardIdm: String) {
            scans.removeAll { it.cardIdm == cardIdm }
        }
    }

    private class InMemoryTransactionDao : TransactionDao {
        private val transactions = mutableListOf<TransactionEntity>()
        private val transactionState = MutableStateFlow<List<TransactionEntity>>(emptyList())
        override suspend fun insertTransaction(transaction: TransactionEntity) {
            transactions += transaction
            transactionState.value = transactions.toList()
        }
        override suspend fun insertTransactions(transactions: List<TransactionEntity>) {
            this.transactions += transactions
            transactionState.value = this.transactions.toList()
        }
        override suspend fun getLatestTransactionByCardIdm(cardIdm: String): TransactionEntity? =
            transactions.filter { it.cardIdm == cardIdm }.maxByOrNull { it.order }
        override suspend fun getTransactionsByCardIdm(cardIdm: String): List<TransactionEntity> =
            transactions.filter { it.cardIdm == cardIdm }.sortedByDescending { it.order }
        override fun observeTransactionsByCardIdm(cardIdm: String) = transactionState.map { records ->
            records.filter { it.cardIdm == cardIdm }.sortedByDescending { it.order }
        }
        override suspend fun getLastOrder(): Int? = transactions.maxOfOrNull { it.order }
        override suspend fun deleteTransactionsByCardIdm(cardIdm: String) {
            transactions.removeAll { it.cardIdm == cardIdm }
            transactionState.value = transactions.toList()
        }
    }
}
