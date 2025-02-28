package fr.isen.ines.isensmartcompanion.ui.screens

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.YearMonth

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen() {
    val events = remember { mutableStateMapOf<LocalDate, MutableList<String>>() }
    var newEvent by remember { mutableStateOf(TextFieldValue("")) }
    val selectedDate = remember { mutableStateOf(LocalDate.now()) }
    val context = LocalContext.current
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val daysInMonth = remember { mutableStateOf((1..currentMonth.lengthOfMonth()).map { currentMonth.atDay(it) }) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calendrier") },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFFFFC0CB))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = {
                    currentMonth = currentMonth.minusMonths(1)
                    daysInMonth.value = (1..currentMonth.lengthOfMonth()).map { currentMonth.atDay(it) }
                }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Mois précédent")
                }
                Text(currentMonth.month.name + " " + currentMonth.year)
                IconButton(onClick = {
                    currentMonth = currentMonth.plusMonths(1)
                    daysInMonth.value = (1..currentMonth.lengthOfMonth()).map { currentMonth.atDay(it) }
                }) {
                    Icon(Icons.Filled.ArrowForward, contentDescription = "Mois suivant")
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(daysInMonth.value.size) { index ->
                    val day = daysInMonth.value[index]
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                if (day == selectedDate.value) Color(0xFFFF69B4) else Color.Transparent,
                                shape = RoundedCornerShape(50)
                            )
                            .clickable { selectedDate.value = day },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = day.dayOfMonth.toString(), color = Color.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = newEvent,
                onValueChange = { newEvent = it },
                label = { Text("Ajouter un cours ou événement") },
                colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color(0xFFFF69B4))
            )
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (newEvent.text.isNotEmpty()) {
                        events.getOrPut(selectedDate.value) { mutableListOf() }.add(newEvent.text)
                        newEvent = TextFieldValue("")
                    } else {
                        Toast.makeText(context, "Entrez un nom pour l'événement", Toast.LENGTH_SHORT).show()
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
                items(events[selectedDate.value] ?: emptyList()) { event ->
                    EventItem(event) {
                        events[selectedDate.value]?.remove(event)
                    }
                }
            }
        }
    }
}

@Composable
fun EventItem(event: String, onDelete: () -> Unit) {
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
            Text(text = event, style = MaterialTheme.typography.bodyLarge, color = Color.Black)
            Button(onClick = onDelete, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF69B4))) {
                Text("Valider", color = Color.White)
            }
        }
    }
}