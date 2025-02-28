package fr.isen.ines.isensmartcompanion.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class EventsViewModel : ViewModel() {

    private val _events = MutableStateFlow<List<EventModel>>(emptyList())
    val events: StateFlow<List<EventModel>> = _events

    private val eventApi = EventApi.create()

    fun fetchEvents() {
        viewModelScope.launch {
            try {
                val response: Response<List<EventModel>> = eventApi.getEvents()
                Log.d("API", "Réponse reçue: ${response.code()} - ${response.message()}")

                if (response.isSuccessful) {
                    val data = response.body()
                    Log.d("API", "Données reçues: $data")
                    _events.value = data ?: emptyList()
                } else {
                    Log.e("API", "Erreur HTTP: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("API", "Exception: ${e.message}")
            }
        }
    }
}
