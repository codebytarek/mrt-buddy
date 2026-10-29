package net.adhikary.mrtbuddy.nfc

import android.nfc.tech.NfcF
import net.adhikary.mrtbuddy.model.Transaction
import net.adhikary.mrtbuddy.nfc.parser.TransactionParser

class NfcReader {
    private val commandGenerator = NfcCommandGenerator()

    fun readTransactionHistory(nfcF: NfcF): List<Transaction> {
        val transactions = mutableListOf<Transaction>()
        val idm = nfcF.tag.id

        val command = commandGenerator.generateReadCommand(idm)
        val response1 = nfcF.transceive(command)
        transactions.addAll(TransactionParser.parseTransactionResponse(response1))

        // Read the next 10 blocks supported by the card's transaction service.
        val command2 = commandGenerator.generateReadCommand(idm, startBlockNumber = 10)
        val response2 = nfcF.transceive(command2)
        transactions.addAll(TransactionParser.parseTransactionResponse(response2))

        return transactions
    }
}
