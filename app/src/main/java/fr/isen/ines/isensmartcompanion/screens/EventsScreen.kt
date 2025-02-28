package fr.isen.ines.isensmartcompanion.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.isen.ines.isensmartcompanion.screens.EventDetailActivity
import fr.isen.ines.isensmartcompanion.screens.EventModel
import fr.isen.ines.isensmartcompanion.screens.EventsViewModel

@Composable
fun EventsScreen(viewModel: EventsViewModel = remember { EventsViewModel() }) {
    val context = LocalContext.current
    val events by viewModel.events.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchEvents()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFE4E1))
            .padding(16.dp),
    ) {
        Text(
            text = "Événements ISEN",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFFFF69B4),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (events.isEmpty()) {
            Text(
                text = "Aucun événement disponible.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(events) { event ->
                    EventCard(event, context)
                }
            }
        }
    }
}

@Composable
fun EventCard(event: EventModel, context: Context) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = event.title, style = MaterialTheme.typography.titleMedium, color = Color.Black)
            Text(text = "📅 ${event.date}", style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
            Text(text = "📍 ${event.location}", style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)

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
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF69B4))
            ) {
                Text("Voir plus", color = Color.White)
            }
        }
    }
}
