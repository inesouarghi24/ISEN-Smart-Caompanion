package fr.isen.ines.isensmartcompanion.screens

import AddEventOrCourseDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    navController: NavHostController,
    eventsViewModel: EventsViewModel = viewModel(),
    customEventViewModel: CustomEventViewModel = viewModel(),
    courseViewModel: CourseViewModel = viewModel()
) {
    val isDarkMode = isSystemInDarkTheme()

    val events by eventsViewModel.events.collectAsState(initial = emptyList())
    val customEvents by customEventViewModel.customEvents.collectAsState(initial = emptyList())
    val courses by courseViewModel.courses.collectAsState()

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDialog by remember { mutableStateOf(false) }
    var isCourse by remember { mutableStateOf(false) }

    LaunchedEffect(selectedDate, courses) {
        courseViewModel.fetchCourses(selectedDate.toString())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "📅 Calendrier 📅",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD81B60),
                    )
                },
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isDarkMode) Color.DarkGray else Color(0xFFFFE4E1), shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = {
                            selectedDate = selectedDate.minusMonths(1)
                        }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Mois précédent",
                                tint = if (isDarkMode) Color.White else Color.Black
                            )
                        }
                        Text(
                            "${selectedDate.month.name} ${selectedDate.year}",
                            color = if (isDarkMode) Color.White else Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = {
                            selectedDate = selectedDate.plusMonths(1)
                        }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Mois suivant",
                                tint = if (isDarkMode) Color.White else Color.Black
                            )
                        }
                    }

                    LazyVerticalGrid(columns = GridCells.Fixed(7), modifier = Modifier.fillMaxWidth()) {
                        items((1..selectedDate.lengthOfMonth()).map { selectedDate.withDayOfMonth(it) }) { day ->
                            val hasEvent = hasEventOrCourse(day, customEvents, courses)

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
                                    Text(
                                        text = day.dayOfMonth.toString(),
                                        color = if (isDarkMode) Color.White else Color.Black
                                    )
                                    if (hasEvent) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(Color.Red, shape = RoundedCornerShape(50))
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        isCourse = false
                        showDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF69B4))
                ) {
                    Text("Ajouter un événement", color = Color.White)
                }

                Button(
                    onClick = {
                        isCourse = true
                        showDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
                ) {
                    Text("Ajouter un cours", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(customEvents.filter { it.date == selectedDate.toString() }) { event ->
                    Text(text = "📅 Événement : ${event.title} - ${event.location}")
                }
                items(courses.filter { it.date == selectedDate.toString() }) { course ->
                    Text(text = "📖 Cours : ${course.subject} - Salle : ${course.room} - Heure : ${course.time}")
                }
            }

            if (showDialog) {
                AddEventOrCourseDialog(
                    isCourse = isCourse,
                    onAddEvent = { eventTitle, eventLocation ->
                        customEventViewModel.addCustomEvent(eventTitle, selectedDate.toString(), "Ajouté", eventLocation)
                        courseViewModel.fetchCourses(selectedDate.toString())
                    },
                    onAddCourse = { time, room, subject ->
                        courseViewModel.addCourse(selectedDate.toString(), time, room, subject)
                        courseViewModel.fetchCourses(selectedDate.toString())
                    },
                    onDismiss = { showDialog = false }
                )
            }
        }
    }
}

fun hasEventOrCourse(day: LocalDate, events: List<CustomEventEntity>, courses: List<CourseEntity>): Boolean {
    return events.any { it.date == day.toString() } || courses.any { it.date == day.toString() }
}
