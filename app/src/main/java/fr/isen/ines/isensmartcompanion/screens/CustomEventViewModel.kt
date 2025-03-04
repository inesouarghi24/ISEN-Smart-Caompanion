package fr.isen.ines.isensmartcompanion.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fr.isen.ines.isensmartcompanion.database.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CustomEventViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).customEventDao()

    private val _customEvents = MutableStateFlow<List<CustomEventEntity>>(emptyList())
    val customEvents: StateFlow<List<CustomEventEntity>> = _customEvents.asStateFlow()

    init {
        fetchCustomEvents()
    }

    fun fetchCustomEvents() {
        viewModelScope.launch {
            _customEvents.value = dao.getAllCustomEvents()
        }
    }

    fun addCustomEvent(title: String, date: String, description: String, location: String) {
        viewModelScope.launch {
            val event = CustomEventEntity(title = title, date = date, description = description, location = location)
            dao.insertCustomEvent(event)
            fetchCustomEvents()
        }
    }


    fun removeEvent(eventId: String) {

        viewModelScope.launch {
            dao.deleteEvent(eventId)
            fetchCustomEvents() // Rafraîchit la liste après suppression
        }
    }
}

