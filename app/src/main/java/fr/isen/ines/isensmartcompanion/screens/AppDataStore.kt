package fr.isen.ines.isensmartcompanion

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension pour créer DataStore
private val Context.dataStore by preferencesDataStore(name = "settings")

class AppDataStore private constructor(context: Context) {
    private val dataStore = context.dataStore // Correction ici ✅

    companion object {
        private val SELECTED_LANGUAGE = stringPreferencesKey("selected_language")
        private var INSTANCE: AppDataStore? = null

        fun getInstance(context: Context): AppDataStore {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppDataStore(context).also { INSTANCE = it }
            }
        }
    }

    val selectedLanguage: Flow<String> = dataStore.data.map { preferences ->
        preferences[SELECTED_LANGUAGE] ?: "Français"
    }

    suspend fun setLanguage(language: String) {
        dataStore.edit { preferences ->
            preferences[SELECTED_LANGUAGE] = language
        }
    }
}
