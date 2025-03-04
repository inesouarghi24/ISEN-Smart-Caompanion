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
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModelProvider
import coil.compose.rememberAsyncImagePainter
import fr.isen.ines.isensmartcompanion.notifications.NotificationReceiver
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
                    onSetReminder = { checkAndRequestExactAlarmPermission(eventName) }
                )
            }
        }
    }

    // 📌 Demande de permission pour les alarmes précises (API 31+)
    private fun checkAndRequestExactAlarmPermission(eventName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(AlarmManager::class.java)
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:$packageName"))
                startActivity(intent)
            } else {
                scheduleNotification(eventName)
            }
        } else {
            scheduleNotification(eventName)
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleNotification(eventName: String) {
        val intent = Intent(this, NotificationReceiver::class.java).apply {
            putExtra("eventName", eventName)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this, eventName.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerTime = System.currentTimeMillis() + 10_000 // 🔥 Déclenche après 10 secondes

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(AlarmManager::class.java)
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
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
    onBack: () -> Unit, onSetReminder: () -> Unit
) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFFC0CB))
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = name, fontSize = 24.sp, color = Color(0xFFD81B60))
            Text(text = "📅 Date : $date", fontSize = 18.sp, color = Color.Black)
            Text(text = "📍 Lieu : $location", fontSize = 18.sp, color = Color.Black)
            Text(text = "📝 Description :", fontSize = 18.sp, color = Color(0xFFD81B60))
            Text(text = description, fontSize = 16.sp, color = Color.Black)

            Button(
                onClick = {
                    if (!isReminderSet) {
                        isReminderSet = true
                        onSetReminder()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = if (isReminderSet) Color.Gray else Color(0xFFD81B60))
            ) {
                Text(if (isReminderSet) "Rappel activé ✅" else "Activer rappel ⏰")
            }
        }
    }
}
