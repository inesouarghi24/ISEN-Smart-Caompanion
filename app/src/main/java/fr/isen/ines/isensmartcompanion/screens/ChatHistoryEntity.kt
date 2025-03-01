package fr.isen.ines.isensmartcompanion.screens

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_history")
data class ChatHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val question: String,
    val answer: String,
    val date: Long = System.currentTimeMillis()
)
