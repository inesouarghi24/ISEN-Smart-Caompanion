package fr.isen.ines.isensmartcompanion.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import fr.isen.ines.isensmartcompanion.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenView(navController: NavHostController, themeViewModel: ThemeViewModel) {
    val isDarkMode by themeViewModel.isDarkMode.collectAsState(initial = false)

    val backgroundColor = if (isDarkMode) Color.Black else Color(0xFFFFE4E1)
    val cardColor = if (isDarkMode) Color.DarkGray else Color(0xFFFFB6C1)
    val textColor = if (isDarkMode) Color.White else Color.Black

    val context = LocalContext.current
    val question = remember { mutableStateOf(TextFieldValue("")) }
    val responses = remember { mutableStateListOf<Pair<String, Boolean>>() }
    val aiManager = remember { GeminiAIManager(context) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "\uD83C\uDF80 ISEN Smart Companion \uD83C\uDF80",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD81B60)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFC0CB)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.isenlogo),
                contentDescription = "ISEN logo",
                tint = textColor,
                modifier = Modifier.size(40.dp)
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
                        ChatBubble(message, isUser, cardColor, textColor)
                    }
                }
            }

            SimpleBottomBar(question.value, { question.value = it }, { userInput ->
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
            }, textColor)
        }
    }
}

@Composable
fun ChatBubble(message: String, isUser: Boolean, cardColor: Color, textColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor),
            modifier = Modifier.padding(4.dp)
        ) {
            Text(
                text = message,
                color = textColor,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
fun SimpleBottomBar(
    question: TextFieldValue,
    onQuestionChange: (TextFieldValue) -> Unit,
    onResponseChange: (TextFieldValue) -> Unit,
    textColor: Color
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
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                cursorColor = textColor,
                disabledIndicatorColor = Color.Transparent
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
