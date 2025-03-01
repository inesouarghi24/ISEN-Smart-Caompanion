package fr.isen.ines.isensmartcompanion.screens

import androidx.lifecycle.ViewModel
import fr.isen.ines.isensmartcompanion.screens.EventModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CustomEventViewModel : ViewModel() {
    private val _customEvents = MutableStateFlow<List<EventModel>>(emptyList())
    val customEvents: StateFlow<List<EventModel>> = _customEvents.asStateFlow()

    fun addCustomEvent(title: String, date: String, description: String, location: String) {
        _customEvents.value = _customEvents.value + EventModel(
            title = title,
            date = date,
            description = description,
            location = location,
            isCustom = true
        )
    }

    fun removeEvent(eventId: String) {
        _customEvents.value = _customEvents.value.filter { it.id != eventId }
    }
}
