package net.adhikary.mrtbuddy.data

import androidx.room.Embedded

/** A card and its latest recorded balance, observed from both Room tables. */
data class CardEntityWithBalance(
    @Embedded val card: CardEntity,
    val balance: Int?
)
