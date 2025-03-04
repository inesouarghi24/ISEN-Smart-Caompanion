import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.isen.ines.isensmartcompanion.database.AppDatabase
import fr.isen.ines.isensmartcompanion.screens.ChatHistoryEntity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreenView() {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val dao = remember { database.chatHistoryDao() }
    val scope = rememberCoroutineScope()

    val messages by dao.getAllMessages().collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFE4E1))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "💖 Historique des questions et réponses 💖",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFFFF1493)
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (messages.isNotEmpty()) {
            Button(
                onClick = { scope.launch { dao.clearHistory() } },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF69B4)), // Rose bonbon 🌷
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Supprimer tout", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Effacer tout l'historique", color = Color.White)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (messages.isEmpty()) {
            Text(text = "😢 Aucune question/réponse enregistrée.", color = Color.Gray)
        } else {
            LazyColumn {
                items(messages) { message ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFB6C1)), // Fond rose pastel 🌸
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("❓ Question: ${message.question}", fontWeight = FontWeight.Bold)
                                Text("💡 Réponse: ${message.answer}")
                                Text("📅 Date: ${formatDate(message.date)}", color = Color.Gray)
                            }

                            // ✅ Bouton pour supprimer une seule entrée
                            IconButton(
                                onClick = { scope.launch { dao.deleteMessage(message.id) } },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
