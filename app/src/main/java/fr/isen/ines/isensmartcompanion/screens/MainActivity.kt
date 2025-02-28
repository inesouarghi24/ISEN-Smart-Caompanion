package fr.isen.ines.isensmartcompanion.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.isen.ines.isensmartcompanion.ui.screens.CalendarScreen
import fr.isen.ines.isensmartcompanion.ui.screens.HomeScreenView
import fr.isen.ines.isensmartcompanion.ui.screens.EventsScreen
import fr.isen.ines.isensmartcompanion.ui.screens.HistoryScreenView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainApp()
        }
    }
}
@Composable
fun MainApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController, navBarItems) }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") { HomeScreenView() }
            composable("history") { HistoryScreenView() }
            composable("events") { EventsScreen() }
            composable("calendar") { CalendarScreen() }
        }
    }
}
@Composable
fun SimpleBottomBar(question: MutableState<String>, onResponseChange: (String) -> Unit) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFFFC0CB)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = question.value,
            onValueChange = { question.value = it },
            modifier = Modifier.weight(1f).padding(start = 8.dp),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = {
                Toast.makeText(context, "Question Submitted", Toast.LENGTH_SHORT).show()
                onResponseChange("Réponse : ${question.value}")
            },
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF69B4))
        ) {
            Text("➜", color = Color.White)
        }
    }
}

@Composable
fun EventCard(event: EventModel, context: Context) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Événement",
                    tint = Color(0xFFFF69B4),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Color.Black
                )
            }

            Text(
                text = "📅 ${event.date}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )
            Text(
                text = "📍 ${event.location}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            OutlinedButton(
                onClick = {
                    val intent = Intent(context, EventDetailActivity::class.java).apply {
                        putExtra("eventName", event.title)
                        putExtra("eventDescription", event.description)
                        putExtra("eventDate", event.date)
                        putExtra("eventLocation", event.location)
                    }
                    context.startActivity(intent)
                },
                border = BorderStroke(1.dp, Color(0xFFFF69B4)),
                shape = RoundedCornerShape(50),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Voir plus", color = Color(0xFFFF69B4))
            }
        }
    }
}
