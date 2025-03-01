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
    val events by eventsViewModel.events.collectAsState(initial = emptyList()) // Fix: Initialisation correcte
    val customEvents by customEventViewModel.customEvents.collectAsState(initial = emptyList())
    var showCustomEvents by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        eventsViewModel.fetchEvents()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Événements") },
                actions = {
                    IconButton(onClick = { showCustomEvents = !showCustomEvents }) {
                        Icon(Icons.Default.Menu, contentDescription = "Filtrer")
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFFFFC0CB))
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            val displayedEvents: List<EventModel> = if (showCustomEvents) customEvents else events // Fix: Type sécurisé

            if (displayedEvents.isEmpty()) {
                Text(
                    text = if (showCustomEvents) "Aucun événement personnalisé" else "Aucun événement disponible",
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp)
                )
            } else {
                LazyColumn {
                    items(displayedEvents) { event ->
                        EventCard(event, context, customEventViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun EventCard(event: EventModel, context: Context, customEventViewModel: CustomEventViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = event.title, style = MaterialTheme.typography.titleMedium, color = Color.Black)
            Text(text = "📅 ${event.date}", style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
            Text(text = "📍 ${event.location}", style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)

            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF69B4))
                ) {
                    Text("Voir plus", color = Color.White)
                }

                if (event.isCustom) {
                    Button(
                        onClick = { customEventViewModel.removeEvent(event.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Supprimer", color = Color.White)
                    }
                }
            }
        }
    }
}
