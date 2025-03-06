package fr.isen.ines.isensmartcompanion.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import fr.isen.ines.isensmartcompanion.notifications.NotificationHelper
import fr.isen.ines.isensmartcompanion.ui.theme.ISENSmartCompanionTheme

class EventDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel()

        val eventName = intent.getStringExtra("eventName") ?: "Nom inconnu"
        val eventDescription = intent.getStringExtra("eventDescription") ?: "Pas de description"
        val eventDate = intent.getStringExtra("eventDate") ?: "Date inconnue"
        val eventLocation = intent.getStringExtra("eventLocation") ?: "Lieu inconnu"

        setContent {
            val themeViewModel: ThemeViewModel = ViewModelProvider(
                this, ThemeViewModelFactory(applicationContext)
            )[ThemeViewModel::class.java]
            val isDarkMode by themeViewModel.isDarkMode.collectAsState(initial = false)

            ISENSmartCompanionTheme(darkTheme = isDarkMode) {
                EventDetailScreen(
                    name = eventName,
                    description = eventDescription,
                    date = eventDate,
                    location = eventLocation,
                    onBack = { finish() },
                    onSetReminder = { NotificationHelper.scheduleNotification(this, eventName) },
                    onInviteFriends = { sendEmailInvitation(eventName, eventDescription, eventDate, eventLocation) }
                )
            }
        }
    }

    private fun sendEmailInvitation(eventName: String, eventDescription: String, eventDate: String, eventLocation: String) {
        val subject = "📅 Invitation à l'événement: $eventName"
        val body = """
            Salut !
            
            Je t'invite à l'événement suivant :
            
            📌 **$eventName**
            📅 Date : $eventDate
            📍 Lieu : $eventLocation
            
            Description :
            $eventDescription
            
            Viens avec nous, ça va être super ! 🎉

            À bientôt !
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }

        try {
            startActivity(Intent.createChooser(intent, "📩 Envoyer l'invitation via..."))
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Aucune application email trouvée", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "event_channel",
                "Rappels d'événements",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal pour rappeler les événements programmés"
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    name: String, description: String, date: String, location: String,
    onBack: () -> Unit, onSetReminder: () -> Unit, onInviteFriends: () -> Unit
) {
    val isDarkMode = isSystemInDarkTheme() // ✅ Détection du mode sombre
    var isReminderSet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Détails de l'événement", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Retour", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDarkMode) Color.Black else Color(0xFFFFC0CB)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDarkMode) Color.Black else Color.White)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = name, fontSize = 24.sp, color = if (isDarkMode) Color.White else Color(0xFFD81B60))
            Text(text = "📅 Date : $date", fontSize = 18.sp, color = if (isDarkMode) Color.LightGray else Color.Black)
            Text(text = "📍 Lieu : $location", fontSize = 18.sp, color = if (isDarkMode) Color.LightGray else Color.Black)
            Text(text = "📝 Description :", fontSize = 18.sp, color = if (isDarkMode) Color.White else Color(0xFFD81B60))
            Text(text = description, fontSize = 16.sp, color = if (isDarkMode) Color.LightGray else Color.Black)

            Button(
                onClick = {
                    if (!isReminderSet) {
                        isReminderSet = true
                        onSetReminder()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = if (isReminderSet) Color.Gray else if (isDarkMode) Color.Magenta else Color(0xFFD81B60))
            ) {
                Text(if (isReminderSet) "Rappel activé ✅" else "Activer rappel ⏰")
            }

            Button(
                onClick = onInviteFriends,
                colors = ButtonDefaults.buttonColors(containerColor = if (isDarkMode) Color.DarkGray else Color(0xFFFFC0CB))
            ) {
                Text("📩 Inviter des amis", color = if (isDarkMode) Color.White else Color.Black)
            }
        }
    }
}
