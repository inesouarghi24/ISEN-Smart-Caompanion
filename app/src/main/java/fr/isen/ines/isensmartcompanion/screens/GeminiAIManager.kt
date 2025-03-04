package fr.isen.ines.isensmartcompanion.screens

import android.content.Context
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import fr.isen.ines.isensmartcompanion.R
import fr.isen.ines.isensmartcompanion.database.AppDatabase
import fr.isen.ines.isensmartcompanion.screens.ChatHistoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiAIManager(context: Context) {
    private val apiKey: String = context.getString(R.string.gemini_api_key).trim()
    private val generativeModel = GenerativeModel(modelName = "gemini-1.5-flash", apiKey = apiKey)
    private val chatHistoryDao = AppDatabase.getDatabase(context).chatHistoryDao()

    suspend fun analyzeText(userInput: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(userInput)
                val text = response.text ?: "Aucune réponse générée"

                chatHistoryDao.insertMessage(ChatHistoryEntity(question = userInput, answer = text))

                text
            } catch (e: Exception) {
                e.printStackTrace()
                "Erreur API : ${e.localizedMessage}"
            }
        }
    }
}
