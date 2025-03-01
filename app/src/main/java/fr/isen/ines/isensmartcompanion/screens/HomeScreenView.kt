package fr.isen.ines.isensmartcompanion.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fr.isen.ines.isensmartcompanion.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenView(navController: NavHostController) {
    val context = LocalContext.current
    val question = remember { mutableStateOf(TextFieldValue("")) }
    val responses = remember { mutableStateListOf<Pair<String, Boolean>>() }
    val aiManager = remember { GeminiAIManager(context) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ISEN Smart Companion", color = Color.White) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFFFFC0CB))
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Iconisen(
                painter = painterResource(id = R.drawable.isenlogo),
                contentDescription = "isen logo",
                modifier = Modifier
                    .size(40.dp)

            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                reverseLayout = false
            ) {
                items(responses) { (message, isUser) ->
                    AnimatedVisibility(visible = message.isNotEmpty()) {
                        ChatBubble(message, isUser)
                    }
                }
            }

            SimpleBottomBar(
                question.value,
                { question.value = it },
                { userInput ->
                    coroutineScope.launch {
                        if (userInput.text.isNotEmpty()) {
                            responses.add(Pair(userInput.text, true))
                            val aiResponse = aiManager.analyzeText(userInput.text)
                            responses.add(Pair(aiResponse, false))
                            question.value = TextFieldValue("")
                        } else {
                            Toast.makeText(context, "Veuillez entrer votre message", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun Iconisen(painter: Painter, contentDescription: String, modifier: Modifier) {
    Icon(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier
    )
}


@Composable
fun ChatBubble(message: String, isUser: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!isUser) {
            Icon(
                painter = painterResource(id = R.drawable.hellokitty),
                contentDescription = "Hello kitty logo",
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Gray)
                    .padding(8.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) Color(0xFFFF69B4) else Color.White
            ),
            modifier = Modifier.padding(4.dp)
        ) {
            Text(
                text = message,
                color = if (isUser) Color.White else Color.Black,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
fun SimpleBottomBar(
    question: TextFieldValue,
    onQuestionChange: (TextFieldValue) -> Unit,
    onResponseChange: (TextFieldValue) -> Unit
) {
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
            value = question,
            onValueChange = onQuestionChange,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = {
                if (question.text.isNotEmpty()) {
                    Toast.makeText(context, "Envoi à l'IA...", Toast.LENGTH_SHORT).show()
                    onResponseChange(question)
                } else {
                    Toast.makeText(context, "Entrez un message avant d'envoyer.", Toast.LENGTH_SHORT).show()
                }
            },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF69B4))
        ) {
            Text("➜", color = Color.White)
        }
    }
}
