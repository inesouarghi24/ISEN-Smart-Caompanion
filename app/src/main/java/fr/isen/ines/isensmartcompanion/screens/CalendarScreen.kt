package fr.isen.ines.isensmartcompanion.screens

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    navController: NavController,
    tabs: List<NavBarItem>
) {
    val events = remember { mutableStateListOf<Pair<String, String>>() }
    val newEvent = remember { mutableStateOf(TextFieldValue("")) }
    val selectedDate = remember { mutableStateOf(LocalDate.now()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calendrier") },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFFFFC0CB))
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Sélectionnez une date :", style = MaterialTheme.typography.headlineMedium)
            Text(selectedDate.value.format(DateTimeFormatter.ofPattern("dd MMM yyyy")), color = Color(0xFFFF69B4))
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = newEvent.value,
                onValueChange = { newEvent.value = it },
                label = { Text("Ajouter un cours ou événement") },
                colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color(0xFFFF69B4))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    if (newEvent.value.text.isNotEmpty()) {
                        events.add(Pair(selectedDate.value.toString(), newEvent.value.text))
                        newEvent.value = TextFieldValue("")
                    } else {
                        Toast.makeText(navController.context, "Entrez un nom pour l'événement", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF69B4))
            ) {
                Text("Ajouter", color = Color.White)
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(events) { (date, event) ->
                    EventItem(date, event) {
                        events.remove(Pair(date, event))
                    }
                }
            }
        }
    }
}

@Composable
fun EventItem(date: String, event: String, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = event, style = MaterialTheme.typography.bodyLarge, color = Color.Black)
                Text(text = "📅 $date", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Button(onClick = onDelete, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF69B4))) {
                Text("Supprimer", color = Color.White)
            }
        }
    }
}
