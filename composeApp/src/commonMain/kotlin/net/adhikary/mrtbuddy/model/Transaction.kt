package net.adhikary.mrtbuddy.model

import kotlinx.datetime.LocalDateTime

data class Transaction(
    val fixedHeader: String,
    val timestamp: LocalDateTime,
    val transactionType: String,
    val fromStation: String,
    val toStation: String,
    val balance: Int,
    val trailing: String
)

data class TransactionWithAmount(
    val transaction: Transaction,
    val amount: Int?
)

sealed class TransactionType {
    data object CommuteHatirjheelBusStart : TransactionType()
    data object CommuteHatirjheelBusEnd : TransactionType()
    data object CommuteDhakaMetro : TransactionType()
    data object CommuteUnknown : TransactionType()
    data object BalanceUpdate : TransactionType()

    companion object {
        fun fromHeader(fixedHeader: String): TransactionType = when (fixedHeader) {
            "42 D6 30 00" -> CommuteHatirjheelBusEnd
            "08 D2 20 00" -> CommuteHatirjheelBusStart
            "08 52 10 00" -> CommuteDhakaMetro
            "1D 60 02 01", "42 60 02 00" -> BalanceUpdate
            else -> CommuteUnknown
        }

        /** Unifies balance-update records and card Recharge station records as one add-money event. */
        fun isRecharge(fixedHeader: String, fromStation: String, toStation: String): Boolean =
            fromHeader(fixedHeader) == BalanceUpdate ||
                fromStation.equals("Recharge", ignoreCase = true) ||
                toStation.equals("Recharge", ignoreCase = true) ||
                (fromStation.isBlank() && toStation.isBlank())
    }
}

sealed class CardState {
    data class Balance(val amount: Int) : CardState()
    data object WaitingForTap : CardState()
    data object Reading : CardState()
    data class Error(val message: String) : CardState()
    data object NoNfcSupport : CardState()
    data object NfcDisabled : CardState()
}

data class CardReadResult(
    val idm: String,
    val transactions: List<Transaction>
)
