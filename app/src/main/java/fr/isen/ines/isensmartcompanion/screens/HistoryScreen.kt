import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.isen.ines.isensmartcompanion.screens.AppDatabase
import fr.isen.ines.isensmartcompanion.screens.ChatHistoryEntity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun HistoryScreenView() {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val dao = remember { database.chatHistoryDao() }

    // Correction: Utilisation de collectAsState() pour observer les changements de Flow
    val messages by dao.getAllMessages().collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFE4E1))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Historique des questions et réponses",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFFFF69B4)
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (messages.isEmpty()) {
            Text(text = "Aucune question/réponse enregistrée.", color = Color.Gray)
        } else {
            LazyColumn {
                items(messages) { message ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Question: ${message.question}", fontWeight = FontWeight.Bold)
                            Text("Réponse: ${message.answer}")
                            Text("Date: ${message.date}", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}
