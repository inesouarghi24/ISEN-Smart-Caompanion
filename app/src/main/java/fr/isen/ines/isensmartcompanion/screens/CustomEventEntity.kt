package fr.isen.ines.isensmartcompanion.screens

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_events")
data class CustomEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val date: String,
    val description: String,
    val location: String
)
