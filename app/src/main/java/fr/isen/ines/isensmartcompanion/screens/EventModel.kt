package fr.isen.ines.isensmartcompanion.screens

import java.util.UUID

data class EventModel(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val date: String,
    val description: String,
    val location: String,
    val isCustom: Boolean = false
)

