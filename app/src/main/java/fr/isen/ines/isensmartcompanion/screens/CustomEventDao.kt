package fr.isen.ines.isensmartcompanion.screens

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CustomEventDao {
    @Query("SELECT * FROM custom_events")
    suspend fun getAllCustomEvents(): List<CustomEventEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomEvent(event: CustomEventEntity)

    @Query("DELETE FROM custom_events WHERE id = :eventId")
    suspend fun deleteEvent(eventId: Int)
}
