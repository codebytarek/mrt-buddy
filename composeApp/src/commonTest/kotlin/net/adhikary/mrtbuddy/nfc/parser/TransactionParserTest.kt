package net.adhikary.mrtbuddy.nfc.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class TransactionParserTest {
    @Test
    fun parsesAvailableRecordEvenWhenItPredatesTheOldHardCodedCutoff() {
        val records = TransactionParser.parseTransactionResponse(responseWith(transactionBlock(yearOffset = 18)))

        assertEquals(1, records.size)
        assertEquals("Farmgate", records.single().fromStation)
        assertEquals("Karwan Bazar", records.single().toStation)
        assertEquals(960, records.single().balance)
        assertTrue(records.single().timestamp.year < 2020)
    }

    @Test
    fun skipsBlankUnusedBlocksButReturnsGenuinelyEmptyHistory() {
        val emptyResponse = ByteArray(13).apply { this[12] = 0 }
        val blankBlockResponse = responseWith(ByteArray(16))

        assertTrue(TransactionParser.parseTransactionResponse(emptyResponse).isEmpty())
        assertTrue(TransactionParser.parseTransactionResponse(blankBlockResponse).isEmpty())
    }

    @Test
    fun malformedOrRejectedResponsesAreErrorsRatherThanEmptyHistory() {
        assertFailsWith<IllegalArgumentException> {
            TransactionParser.parseTransactionResponse(byteArrayOf(0, 1))
        }

        val rejectedResponse = ByteArray(13).apply {
            this[10] = 1
            this[12] = 0
        }
        assertFailsWith<IllegalStateException> {
            TransactionParser.parseTransactionResponse(rejectedResponse)
        }

        val truncatedResponse = ByteArray(13).apply { this[12] = 1 }
        assertFailsWith<IllegalStateException> {
            TransactionParser.parseTransactionResponse(truncatedResponse)
        }
    }

    private fun responseWith(block: ByteArray): ByteArray = ByteArray(13 + block.size).apply {
        this[10] = 0
        this[11] = 0
        this[12] = 1
        block.copyInto(this, destinationOffset = 13)
    }

    private fun transactionBlock(yearOffset: Int): ByteArray {
        val timestamp = (yearOffset shl 17) or (7 shl 13) or (2 shl 8) or (10 shl 3)
        return ByteArray(16).apply {
            this[0] = 1
            this[1] = 2
            this[2] = 3
            this[3] = 4
            this[4] = (timestamp shr 16).toByte()
            this[5] = (timestamp shr 8).toByte()
            this[6] = timestamp.toByte()
            this[8] = 40 // Farmgate
            this[10] = 35 // Karwan Bazar
            this[11] = 960.toByte()
            this[12] = (960 shr 8).toByte()
            this[13] = (960 shr 16).toByte()
        }
    }
}
