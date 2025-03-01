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

private val Context.dataStore by preferencesDataStore(name = "settings")

class ThemeViewModel(context: Context) : ViewModel() {

    private val dataStore = context.dataStore
    private val darkModeKey = booleanPreferencesKey("dark_mode")

    val isDarkMode: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[darkModeKey] ?: false }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[darkModeKey] = enabled
            }
        }
    }


    suspend fun getDarkMode(): Boolean {
        return isDarkMode.first()
    }
}
