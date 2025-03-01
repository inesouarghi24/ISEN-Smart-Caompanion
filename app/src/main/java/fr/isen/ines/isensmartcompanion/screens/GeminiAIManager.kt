package fr.isen.ines.isensmartcompanion.screens

import android.content.Context
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import fr.isen.ines.isensmartcompanion.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class GeminiAIManager(context: Context) {

    // 🔥 Vérifier si la clé API est bien récupérée
    private val apiKey: String = context.getString(R.string.gemini_api_key).trim()

    // 🔍 Log pour voir si la clé est bien récupérée
    init {
        Log.d("GeminiAPI", "Clé API récupérée : $apiKey")
    }

    private val generativeModel = GenerativeModel(modelName = "gemini-1.5-flash", apiKey = apiKey)

    suspend fun analyzeText(userInput: String): String {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("GeminiAPI", "Envoi du texte à l'API : $userInput")
                val response = generativeModel.generateContent(userInput)
                val text = response.text ?: "Aucune réponse générée"

                Log.d("GeminiAPI", "Réponse API : $text")
                text
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("GeminiAPI", "Erreur API : ${e.localizedMessage}")
                "Erreur API : ${e.localizedMessage ?: "Problème inconnu"}"
            }
        }
    }

    fun testGeminiAPI(): Boolean {
        return try {
            val url = URL("https://generativelanguage.googleapis.com/v1/models/gemini-pro:generateText?key=$apiKey")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            val responseCode = connection.responseCode

            Log.d("GeminiAPI", "Réponse HTTP : $responseCode")

            responseCode == 200
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("GeminiAPI", "Erreur de connexion à l'API : ${e.localizedMessage}")
            false
        }
    }
}
