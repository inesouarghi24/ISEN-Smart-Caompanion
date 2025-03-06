package fr.isen.ines.isensmartcompanion.screens

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    eventsViewModel: EventsViewModel = viewModel(),
    customEventViewModel: CustomEventViewModel = viewModel(),
    context: Context,
    themeViewModel: ThemeViewModel
) {
    val isDarkMode by themeViewModel.isDarkMode.collectAsState(initial = false)
    val backgroundColor = if (isDarkMode) Color.Black else Color(0xFFFFF0F5)
    val cardColor = if (isDarkMode) Color.DarkGray else Color(0xFFFFE4E1)
    val textColor = if (isDarkMode) Color.White else Color(0xFFD81B60)

    val events: List<EventModel> by eventsViewModel.events.collectAsState(initial = emptyList())
    val customEvents = customEventViewModel.customEvents.collectAsState(initial = emptyList()).value

    var showCustomEvents by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        eventsViewModel.fetchEvents()
        customEventViewModel.fetchCustomEvents()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (showCustomEvents) "🎀 Événements Personnalisés 🎀" else "📅 Événements Officiels 📅",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                },
                actions = {
                    IconButton(onClick = { showCustomEvents = !showCustomEvents }) {
                        Icon(Icons.Filled.Menu, contentDescription = "Filtrer", tint = textColor)
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = if (isDarkMode) Color.DarkGray else Color(0xFFFFC0CB)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val displayedEvents: List<EventModel> = if (showCustomEvents) {
                customEvents.map { customEvent ->
                    EventModel(
                        id = customEvent.id.toString(),
                        title = customEvent.title,
                        date = customEvent.date,
                        description = customEvent.description,
                        location = customEvent.location,
                        isCustom = true
                    )
                }
            } else events

            LaunchedEffect(displayedEvents) {
                Log.d("EventsScreen", "Nombre d'événements: ${displayedEvents.size}")
                displayedEvents.forEach { event: EventModel ->
                    Log.d("EventsScreen", "Événement: ${event.title} - ${event.date}")
                }
            }

            if (displayedEvents.isEmpty()) {
                Text(
                    text = if (showCustomEvents) "🎀 Aucun événement personnalisé 🎀" else "📅 Aucun événement disponible 📅",
                    fontSize = 16.sp,
                    color = textColor,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(displayedEvents) { event: EventModel ->
                        EventCard(event, context, showCustomEvents, customEventViewModel, isDarkMode)
                    }
                }
            }
        }
    }
}

@Composable
fun EventCard(event: EventModel, context: Context, isCustom: Boolean, customEventViewModel: CustomEventViewModel, isDarkMode: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = if (isDarkMode) Color.DarkGray else Color(0xFFFFE4E1)),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "🎀 ${event.title}", fontSize = 18.sp, color = if (isDarkMode) Color.White else Color(0xFFD81B60))
            Text(text = "📅 ${event.date}", fontSize = 16.sp, color = if (isDarkMode) Color.LightGray else Color.DarkGray)
            Text(text = "📍 ${event.location}", fontSize = 16.sp, color = if (isDarkMode) Color.LightGray else Color.Gray)

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        val intent = Intent(context, EventDetailActivity::class.java).apply {
                            putExtra("eventName", event.title)
                            putExtra("eventDescription", event.description)
                            putExtra("eventDate", event.date)
                            putExtra("eventLocation", event.location)
                        }
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isDarkMode) Color.Magenta else Color(0xFFFFA6C9)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Détails", color = Color.White)
                }

                if (isCustom) {
                    Button(
                        onClick = { customEventViewModel.removeEvent(event.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isDarkMode) Color.Red else Color(0xFFFF4081)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Supprimer", color = Color.White)
                    }
                }
            }
        }
    }
}
