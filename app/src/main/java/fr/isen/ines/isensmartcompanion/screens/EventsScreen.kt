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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.RoundedCornerShape
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
    val events by eventsViewModel.events.collectAsState()
    val customEvents by customEventViewModel.customEvents.collectAsState()

    var showCustomEvents by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        eventsViewModel.fetchEvents()
        customEventViewModel.fetchCustomEvents()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🎀 Événements 🎀", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor) },
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
            Text(
                text = if (showCustomEvents) "🎀 Événements Personnalisés 🎀" else "📅 Événements Officiels 📅",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                modifier = Modifier.padding(8.dp)
            )

            val displayedEvents = if (showCustomEvents) {
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
                    color = textColor,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(displayedEvents) { event ->
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
