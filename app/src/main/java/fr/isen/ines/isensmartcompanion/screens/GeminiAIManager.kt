package fr.isen.ines.isensmartcompanion.screens

import android.content.Context
import android.content.pm.PackageManager
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiAIManager(context: Context) {

    private val apiKey: String = getApiKeyFromManifest(context)
    private val generativeModel = GenerativeModel(modelName = "gemini-1.5-flash", apiKey = apiKey)

    private fun getApiKeyFromManifest(context: Context): String {
        return try {
            val appInfo = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            appInfo.metaData?.getString("com.google.ai.API_KEY") ?: throw IllegalStateException("Clé API manquante dans AndroidManifest.xml")
        } catch (e: Exception) {
            e.printStackTrace()
            throw IllegalStateException("Erreur lors de la récupération de la clé API : ${e.localizedMessage}")
        }
    }

    suspend fun analyzeText(userInput: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(userInput)
                response.text ?: "Aucune réponse générée"
            } catch (e: Exception) {
                e.printStackTrace()
                "Erreur API : ${e.localizedMessage}"
            }
        }
    }
}
