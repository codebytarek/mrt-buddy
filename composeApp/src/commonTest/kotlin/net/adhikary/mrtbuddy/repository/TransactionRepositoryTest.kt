package net.adhikary.mrtbuddy.repository

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import net.adhikary.mrtbuddy.dao.CardDao
import net.adhikary.mrtbuddy.dao.ScanDao
import net.adhikary.mrtbuddy.dao.TransactionDao
import net.adhikary.mrtbuddy.data.CardEntity
import net.adhikary.mrtbuddy.data.ScanEntity
import net.adhikary.mrtbuddy.data.TransactionEntity
import net.adhikary.mrtbuddy.model.CardReadResult
import net.adhikary.mrtbuddy.model.Transaction

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
        override suspend fun insertCard(card: CardEntity) { cards.putIfAbsent(card.idm, card) }
        override suspend fun updateLastScanTime(cardIdm: String, scanTime: Long) {
            cards[cardIdm]?.let { cards[cardIdm] = it.copy(lastScanTime = scanTime) }
        }
        override suspend fun getCardByIdm(idm: String): CardEntity? = cards[idm]
        override suspend fun getAllCards(): List<CardEntity> = cards.values.toList()
        override suspend fun updateCardName(cardIdm: String, newName: String) {
            cards[cardIdm]?.let { cards[cardIdm] = it.copy(name = newName) }
        }
        override suspend fun deleteCard(cardIdm: String) { cards.remove(cardIdm) }
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
        override suspend fun insertTransaction(transaction: TransactionEntity) {
            transactions += transaction
        }
        override suspend fun insertTransactions(transactions: List<TransactionEntity>) {
            this.transactions += transactions
        }
        override suspend fun getLatestTransactionByCardIdm(cardIdm: String): TransactionEntity? =
            transactions.filter { it.cardIdm == cardIdm }.maxByOrNull { it.order }
        override suspend fun getTransactionsByCardIdm(cardIdm: String): List<TransactionEntity> =
            transactions.filter { it.cardIdm == cardIdm }.sortedByDescending { it.order }
        override suspend fun getLastOrder(): Int? = transactions.maxOfOrNull { it.order }
        override suspend fun deleteTransactionsByCardIdm(cardIdm: String) {
            transactions.removeAll { it.cardIdm == cardIdm }
        }
    }
}
