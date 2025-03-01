package fr.isen.ines.isensmartcompanion.screens

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import fr.isen.ines.isensmartcompanion.ui.theme.ISENSmartCompanionTheme

class EventDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val eventName = intent.getStringExtra("eventName") ?: "Nom inconnu"
        val eventDescription = intent.getStringExtra("eventDescription") ?: "Pas de description"
        val eventDate = intent.getStringExtra("eventDate") ?: "Date inconnue"
        val eventLocation = intent.getStringExtra("eventLocation") ?: "Lieu inconnu"

        enableEdgeToEdge()
        setContent {
            ISENSmartCompanionTheme {
                EventDetailScreen(
                    name = eventName,
                    description = eventDescription,
                    date = eventDate,
                    location = eventLocation,
                    onBack = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(name: String, description: String, date: String, location: String, onBack: () -> Unit) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

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
                    containerColor = Color(0xFFFFC0CB)
                )
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
            Text(text = name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD81B60))
            Text(text = "📅 Date : $date", fontSize = 18.sp, color = Color.Black)
            Text(text = "📍 Lieu : $location", fontSize = 18.sp, color = Color.Black)
            Text(text = "📝 Description :", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD81B60))
            Text(text = description, fontSize = 16.sp, color = Color.Black)

            Spacer(modifier = Modifier.height(16.dp))

            PicturePickerButton { uri ->
                selectedImageUri = uri
            }

            selectedImageUri?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "Image de l'événement",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
        }
    }
}
