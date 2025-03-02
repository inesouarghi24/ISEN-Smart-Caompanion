package fr.isen.ines.isensmartcompanion.screens

import HistoryScreenView
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import fr.isen.ines.isensmartcompanion.ui.theme.ISENSmartCompanionTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var database: AppDatabase
    private lateinit var dao: ChatHistoryDao

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = AppDatabase.getDatabase(this)
        dao = database.chatHistoryDao()

        setContent {
            val eventsViewModel: EventsViewModel = viewModel()
            val customEventViewModel: CustomEventViewModel = viewModel()

            val themeViewModel: ThemeViewModel = ViewModelProvider(
                this, ThemeViewModelFactory(applicationContext)
            )[ThemeViewModel::class.java]

            val isDarkMode by themeViewModel.isDarkMode.collectAsState(initial = false)
            val navController = rememberNavController()

            ISENSmartCompanionTheme(darkTheme = isDarkMode) {
                Scaffold(
                    bottomBar = { BottomNavigationBar(navController, navBarItems) }
                ) { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        composable("home") { HomeScreenView(navController) }
                        composable("history") { HistoryScreenView() }
                        composable("events") { EventsScreen(eventsViewModel, customEventViewModel) }
                        composable("calendar") { CalendarScreen(navController, eventsViewModel) }
                        composable("settings") { SettingsScreen(themeViewModel) }
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
}
