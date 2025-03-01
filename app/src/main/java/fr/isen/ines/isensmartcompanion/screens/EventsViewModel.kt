package fr.isen.ines.isensmartcompanion.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EventsViewModel : ViewModel() {
    private val _events = MutableStateFlow<List<EventModel>>(emptyList())
    val events: StateFlow<List<EventModel>> get() = _events

    init {
        fetchEvents()
    }

    fun fetchEvents() {
        viewModelScope.launch {
            try {
                val response = EventApi.create().getEvents()
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        _events.value = data
                        Log.d("API", "Données reçues: $data")
                    } else {
                        Log.e("API", "Réponse vide")
                    }
                } else {
                    Log.e("API", "Erreur HTTP: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("API", "Exception: ${e.message}")
            }
        }
    }
}
