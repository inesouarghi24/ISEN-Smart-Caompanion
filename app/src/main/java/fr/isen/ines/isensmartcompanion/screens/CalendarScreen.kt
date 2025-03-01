package fr.isen.ines.isensmartcompanion.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.isen.ines.isensmartcompanion.ui.screens.EventCard
import java.time.LocalDate
import java.time.YearMonth
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun <NavHostController> CalendarScreen(navController: NavHostController, viewModel: EventsViewModel = viewModel()) {
    val events by viewModel.events.collectAsState(initial = emptyList())

    val eventMap = remember(events) {
        events.groupBy {
            try {
                LocalDate.parse(it.date)
            } catch (e: Exception) {
                LocalDate.now()
            }
        }
    }

    val context = LocalContext.current
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var daysInMonth by remember { mutableStateOf((1..currentMonth.lengthOfMonth()).map { currentMonth.atDay(it) }) }

    var newEventTitle by remember { mutableStateOf(TextFieldValue("")) }
    var newEventLocation by remember { mutableStateOf(TextFieldValue("")) }
    var userEvents by remember { mutableStateOf(mutableMapOf<LocalDate, MutableList<EventModel>>()) }

    LaunchedEffect(Unit) {
        viewModel.fetchEvents()
    }

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
                    daysInMonth = (1..currentMonth.lengthOfMonth()).map { currentMonth.atDay(it) }
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Mois précédent")
                }
                Text("${currentMonth.month.name} ${currentMonth.year}")
                IconButton(onClick = {
                    currentMonth = currentMonth.plusMonths(1)
                    daysInMonth = (1..currentMonth.lengthOfMonth()).map { currentMonth.atDay(it) }
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Mois suivant")
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(daysInMonth.size) { index ->
                    val day = daysInMonth[index]
                    val hasEvent = eventMap.containsKey(day) || userEvents.containsKey(day)

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                if (day == selectedDate) Color(0xFFFF69B4) else Color.Transparent,
                                shape = RoundedCornerShape(50)
                            )
                            .clickable { selectedDate = day },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = day.dayOfMonth.toString(), color = Color.Black)
                            if (hasEvent) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(Color(0xFFFF69B4), shape = RoundedCornerShape(50))
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val apiEventsForSelectedDay = eventMap[selectedDate] ?: emptyList()
            if (apiEventsForSelectedDay.isNotEmpty()) {
                Text("📅 Événements officiels :", style = MaterialTheme.typography.titleMedium, color = Color(0xFFD81B60))
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(apiEventsForSelectedDay) { event ->
                        EventCard(event, context)
                    }
                }
            }

            val userEventsForSelectedDay = userEvents[selectedDate] ?: emptyList()
            if (userEventsForSelectedDay.isNotEmpty()) {
                Text("📝 Événements personnels :", style = MaterialTheme.typography.titleMedium, color = Color(0xFF00796B))
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(userEventsForSelectedDay) { event ->
                        EventCard(event, context)
                        Button(
                            onClick = {
                                userEvents[selectedDate]?.remove(event)
                                userEvents = userEvents.toMutableMap()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            modifier = Modifier.fillMaxWidth().padding(8.dp)
                        ) {
                            Text("Supprimer", color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Ajouter un événement :", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = newEventTitle,
                onValueChange = { newEventTitle = it },
                label = { Text("Titre de l'événement") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = newEventLocation,
                onValueChange = { newEventLocation = it },
                label = { Text("Lieu de l'événement") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )

            Button(
                onClick = {
                    if (newEventTitle.text.isNotEmpty()) {
                        val newEvent = EventModel(
                            id = UUID.randomUUID().toString(),
                            title = newEventTitle.text,
                            date = selectedDate.toString(),
                            description = "Événement ajouté par l'utilisateur",
                            location = newEventLocation.text,
                            category = "Personnalisé"
                        )
                        userEvents[selectedDate] = (userEvents[selectedDate] ?: mutableListOf()).apply { add(newEvent) }
                        newEventTitle = TextFieldValue("")
                        newEventLocation = TextFieldValue("")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF69B4)),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text("Ajouter", color = Color.White)
            }
        }
    }
}
