package fr.isen.ines.isensmartcompanion.screens

import com.google.gson.annotations.SerializedName

data class EventModel(
    @SerializedName("category") val category: String,
    @SerializedName("date") val date: String,
    @SerializedName("description") val description: String,
    @SerializedName("location") val location: String,
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String
)




