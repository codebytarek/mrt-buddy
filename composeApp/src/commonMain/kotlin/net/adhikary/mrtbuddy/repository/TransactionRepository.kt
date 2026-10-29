package net.adhikary.mrtbuddy.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import net.adhikary.mrtbuddy.dao.CardDao
import net.adhikary.mrtbuddy.dao.ScanDao
import net.adhikary.mrtbuddy.dao.TransactionDao
import net.adhikary.mrtbuddy.data.CardEntity
import net.adhikary.mrtbuddy.data.CardEntityWithBalance
import net.adhikary.mrtbuddy.data.CardTransactionHistory
import net.adhikary.mrtbuddy.data.ScanEntity
import net.adhikary.mrtbuddy.data.TransactionEntity
import net.adhikary.mrtbuddy.data.TransactionEntityWithAmount
import net.adhikary.mrtbuddy.model.CardReadResult
import net.adhikary.mrtbuddy.nfc.service.TimestampService

class TransactionRepository(
    private val cardDao: CardDao,
    private val scanDao: ScanDao,
    private val transactionDao: TransactionDao
) {

    suspend fun saveCardReadResult(result: CardReadResult) {
        val currentTime = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        val cardEntity = CardEntity(idm = result.idm, name = null, lastScanTime = currentTime)
        cardDao.insertCard(cardEntity)
        cardDao.updateLastScanTime(result.idm, currentTime)

        val scanEntity = ScanEntity(cardIdm = result.idm)
        val scanId = scanDao.insertScan(scanEntity)

        val newTransactionEntities = result.transactions.map { txn ->
            val dateTime = txn.timestamp
                .toInstant(TimestampService.getDefaultTimezone())
                .toEpochMilliseconds()

            TransactionEntity(
                cardIdm = result.idm,
                scanId = scanId,
                fromStation = txn.fromStation,
                toStation = txn.toStation,
                balance = txn.balance,
                dateTime = dateTime,
                fixedHeader = txn.fixedHeader
            )
        }

        val lastOrder = transactionDao.getLastOrder() ?: 0
        val transactionsToInsert = newTransactionEntities
            .reversed()
            .mapIndexed { index, entity ->
                entity.copy(order = lastOrder + index + 1)
            }

        transactionDao.insertTransactions(transactionsToInsert)
    }

    suspend fun getCardByIdm(idm: String): CardEntity? {
        return cardDao.getCardByIdm(idm)
    }

    suspend fun getAllCards(): List<CardEntity> {
        return cardDao.getAllCards()
    }

    fun observeCardsWithLatestBalance(): Flow<List<CardEntityWithBalance>> =
        cardDao.observeCardsWithLatestBalance()

    suspend fun getTransactionsByCardIdm(cardIdm: String): List<TransactionEntityWithAmount> {
        val transactions = transactionDao.getTransactionsByCardIdm(cardIdm)

        val sortedTransactions = transactions.sortedByDescending { it.order }
        return withAmounts(sortedTransactions)
    }

    fun observeTransactionHistory(cardIdm: String): Flow<CardTransactionHistory> =
        combine(
            transactionDao.observeTransactionsByCardIdm(cardIdm),
            cardDao.observeCardByIdm(cardIdm)
        ) { transactions, card ->
            val sortedTransactions = transactions.sortedByDescending { it.order }
            CardTransactionHistory(
                transactions = withAmounts(sortedTransactions),
                balance = sortedTransactions.firstOrNull()?.balance,
                cardName = card?.name
            )
        }

    suspend fun getLatestBalanceByCardIdm(cardIdm: String): Int? {
        return transactionDao.getLatestTransactionByCardIdm(cardIdm)?.balance
    }

    suspend fun renameCard(cardIdm: String, newName: String) {
        cardDao.updateCardName(cardIdm, newName)
    }

    suspend fun deleteCard(cardIdm: String) {
        cardDao.deleteCard(cardIdm)
        scanDao.deleteScansByCardIdm(cardIdm)
        transactionDao.deleteTransactionsByCardIdm(cardIdm)
    }

    private fun withAmounts(
        transactions: List<TransactionEntity>
    ): List<TransactionEntityWithAmount> = transactions.mapIndexed { index, transaction ->
        val amount = transactions.getOrNull(index + 1)?.let { older ->
            transaction.balance - older.balance
        }
        TransactionEntityWithAmount(transactionEntity = transaction, amount = amount)
    }
}
