package net.adhikary.mrtbuddy.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.adhikary.mrtbuddy.data.CardEntity
import net.adhikary.mrtbuddy.data.CardEntityWithBalance

@Dao
interface CardDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCard(card: CardEntity)

    @Query("UPDATE cards SET lastScanTime = :scanTime WHERE idm = :cardIdm")
    suspend fun updateLastScanTime(cardIdm: String, scanTime: Long)

    @Query("SELECT * FROM cards WHERE idm = :idm LIMIT 1")
    suspend fun getCardByIdm(idm: String): CardEntity?

    @Query("SELECT * FROM cards WHERE idm = :idm LIMIT 1")
    fun observeCardByIdm(idm: String): Flow<CardEntity?>

    @Query("SELECT * FROM cards")
    suspend fun getAllCards(): List<CardEntity>

    @Query(
        """
        SELECT cards.*,
            (SELECT transactions.balance
             FROM transactions
             WHERE transactions.cardIdm = cards.idm
             ORDER BY transactions.`order` DESC
             LIMIT 1) AS balance
        FROM cards
        ORDER BY cards.lastScanTime DESC
        """
    )
    fun observeCardsWithLatestBalance(): Flow<List<CardEntityWithBalance>>

    @Query("UPDATE cards SET name = :newName WHERE idm = :cardIdm")
    suspend fun updateCardName(cardIdm: String, newName: String)

    @Query("DELETE FROM cards WHERE idm = :cardIdm;")
    suspend fun deleteCard(cardIdm: String)
}
