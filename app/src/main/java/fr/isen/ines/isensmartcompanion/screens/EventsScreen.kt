package fr.isen.ines.isensmartcompanion.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.RoundedCornerShape
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
        eventsViewModel.fetchEvents()
        customEventViewModel.fetchCustomEvents()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🎀 Événements 🎀", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { showCustomEvents = !showCustomEvents }) {
                        Icon(Icons.Default.Menu, contentDescription = "Filtrer", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFFFFA6C9)) // 💖 Rose pastel
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFF0F5)) // 🌸 Fond Rose très clair
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (showCustomEvents) "🎀 Événements Personnalisés 🎀" else "📅 Événements Officiels 📅",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD81B60), // Rose foncé
                modifier = Modifier.padding(8.dp)
            )

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
                    text = if (showCustomEvents) "🎀 Aucun événement personnalisé 🎀" else "📅 Aucun événement disponible 📅",
                    fontSize = 16.sp,
                    color = Color(0xFF7B1FA2), // Violet pastel
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(displayedEvents) { event ->
                        EventCard(event = event, customEventViewModel = customEventViewModel)
                    }
                }
            }
        }
    }
}
