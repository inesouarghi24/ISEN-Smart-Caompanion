package fr.isen.ines.isensmartcompanion.screens

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// 🔹 Extension pour obtenir DataStore proprement
private val Context.dataStore by preferencesDataStore(name = "settings")

class ThemeViewModel(context: Context) : ViewModel() {

    private val dataStore = context.dataStore
    private val darkModeKey = booleanPreferencesKey("dark_mode") // 🔥 Clé correctement définie

    // 🔥 Récupération du mode sombre
    val isDarkMode: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[darkModeKey] ?: false } // 🔹 `false` par défaut

    // 🔥 Sauvegarde du mode sombre
    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[darkModeKey] = enabled
            }
        }
    }

    // 🔥 Récupération immédiate du mode sombre (pour les erreurs éventuelles)
    suspend fun getDarkMode(): Boolean {
        return isDarkMode.first()
    }
}
