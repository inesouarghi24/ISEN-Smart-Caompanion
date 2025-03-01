package fr.isen.ines.isensmartcompanion.screens

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatHistoryDao {

    @Insert
    suspend fun insertMessage(chat: ChatHistoryEntity)

    @Query("SELECT * FROM chat_history ORDER BY date DESC")
    fun getAllMessages(): Flow<List<ChatHistoryEntity>>

    @Query("DELETE FROM chat_history WHERE id = :messageId")
    suspend fun deleteMessage(messageId: Int)

    @Query("DELETE FROM chat_history")
    suspend fun clearHistory()

}
