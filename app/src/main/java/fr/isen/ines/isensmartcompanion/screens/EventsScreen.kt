package fr.isen.ines.isensmartcompanion.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    eventsViewModel: EventsViewModel = viewModel(),
    customEventViewModel: CustomEventViewModel = viewModel()
) {
    val context = LocalContext.current
    val events by eventsViewModel.events.collectAsState(initial = emptyList())
    val customEvents by customEventViewModel.customEvents.collectAsState(initial = emptyList())
    var showCustomEvents by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        eventsViewModel.fetchEvents() // 🔥 Charge les événements API
        customEventViewModel.fetchCustomEvents() // 🔥 Charge les événements personnalisés
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🎀 Événements") },
                actions = {
                    IconButton(onClick = { showCustomEvents = !showCustomEvents }) {
                        Icon(Icons.Default.Menu, contentDescription = "Filtrer")
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFFFFC0CB)) // Rose pastel 🎀
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val displayedEvents: List<EventModel> = if (showCustomEvents) {
                customEvents.map { event ->
                    EventModel(
                        id = event.id.toString(),
                        title = event.title,
                        date = event.date,
                        description = event.description,
                        location = event.location,
                        isCustom = true
                    )
                }
            } else {
                events
            }

            if (displayedEvents.isEmpty()) {
                Text(
                    text = if (showCustomEvents) "Aucun événement personnalisé 😢" else "Aucun événement disponible 📭",
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp),
                    color = Color(0xFFD81B60), // Rose foncé pour rester kawaii 🎀
                    style = MaterialTheme.typography.titleMedium
                )
            } else {
                LazyColumn {
                    items(displayedEvents) { event ->
                        EventCard(event = event, context = context, customEventViewModel = customEventViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun EventCard(event: EventModel, context: Context, customEventViewModel: CustomEventViewModel? = null) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE4E1)), // 🌸 Rose doux
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "🎀 ${event.title}", style = MaterialTheme.typography.titleMedium, color = Color(0xFFD81B60))
            Text(text = "📅 ${event.date}", style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
            Text(text = "📍 ${event.location}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Bouton Détails qui ouvre `EventDetailActivity`
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA6C9)), // Rose pastel
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                ) {
                    Text("Détails", color = Color.White)
                }

                // Bouton Supprimer visible **seulement si l'événement est personnalisé**
                if (event.isCustom) {
                    Button(
                        onClick = { customEventViewModel?.removeEvent(event.id.toInt()) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4081)), // Rouge-rose foncé
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                    ) {
                        Text("Supprimer", color = Color.White)
                    }
                }
            }
        }
    }
}
