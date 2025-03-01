package fr.isen.ines.isensmartcompanion.screens

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import fr.isen.ines.isensmartcompanion.ui.theme.ISENSmartCompanionTheme

class EventDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
