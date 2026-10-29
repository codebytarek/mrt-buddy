package net.adhikary.mrtbuddy.data

data class CardTransactionHistory(
    val transactions: List<TransactionEntityWithAmount>,
    val balance: Int?,
    val cardName: String?
)
