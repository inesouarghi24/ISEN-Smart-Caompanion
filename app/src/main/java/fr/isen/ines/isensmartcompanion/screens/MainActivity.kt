package fr.isen.ines.isensmartcompanion.screens


import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.isen.ines.isensmartcompanion.database.AppDatabase
import fr.isen.ines.isensmartcompanion.ui.theme.ISENSmartCompanionTheme
import kotlinx.coroutines.launch
import fr.isen.ines.isensmartcompanion.screens.navBarItems




class MainActivity : ComponentActivity() {
    private lateinit var database: AppDatabase
    private lateinit var dao: ChatHistoryDao

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = AppDatabase.getDatabase(this)
        dao = database.chatHistoryDao()

        setContent {
            val navController = rememberNavController()
            val eventsViewModel: EventsViewModel = viewModel()
            val customEventViewModel: CustomEventViewModel = viewModel()
            val themeViewModel: ThemeViewModel = ViewModelProvider(
                this, ThemeViewModelFactory(applicationContext)
            )[ThemeViewModel::class.java]

            val isDarkMode by themeViewModel.isDarkMode.collectAsState(initial = false)

            ISENSmartCompanionTheme(darkTheme = isDarkMode) {
                Scaffold(
                    bottomBar = { BottomNavigationBar(navController, navBarItems) }
                ) { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        composable("home") { HomeScreenView(navController, themeViewModel) }
                        composable("history") { HistoryScreenView(themeViewModel) }
                        composable("events") { EventsScreen(eventsViewModel, customEventViewModel, this@MainActivity, themeViewModel) }
                        composable("calendar") { CalendarScreen(navController, eventsViewModel, ) }
                        composable("settings") { SettingsScreen(themeViewModel) }
                    }
                }
            }
        }
    }

    fun saveChatToHistory(question: String, answer: String) {
        lifecycleScope.launch {
            dao.insertMessage(ChatHistoryEntity(question = question, answer = answer))
        }
    }
}


@Composable
fun SimpleBottomBar(question: MutableState<String>, onResponseChange: (String) -> Unit) {
    val context = LocalContext.current
    val isDarkMode = isSystemInDarkTheme()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(if (isDarkMode) Color.DarkGray else Color(0xFFFFC0CB)),
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
                unfocusedContainerColor = Color.Transparent,
                focusedTextColor = if (isDarkMode) Color.White else Color.Black
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = {
                Toast.makeText(context, "Question Submitted", Toast.LENGTH_SHORT).show()
                onResponseChange("Réponse : ${question.value}")
            },
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = if (isDarkMode) Color.Magenta else Color(0xFFFF69B4))
        ) {
            Text("➜", color = Color.White)
        }
    }
}

@Composable
fun EventCard(event: EventModel, context: Context) {
    val isDarkMode = isSystemInDarkTheme()

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = if (isDarkMode) Color.DarkGray else Color.White),
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
                    tint = if (isDarkMode) Color.White else Color(0xFFFF69B4),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = if (isDarkMode) Color.White else Color.Black
                )
            }

            Text(
                text = "📅 ${event.date}",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDarkMode) Color.LightGray else Color.DarkGray
            )
            Text(
                text = "📍 ${event.location}",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDarkMode) Color.LightGray else Color.DarkGray
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
                border = BorderStroke(1.dp, if (isDarkMode) Color.White else Color(0xFFFF69B4)),
                shape = RoundedCornerShape(50),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Voir plus", color = if (isDarkMode) Color.White else Color(0xFFFF69B4))
            }
        }
    }
}


