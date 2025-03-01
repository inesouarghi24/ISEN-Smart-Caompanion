package fr.isen.ines.isensmartcompanion.screens

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import fr.isen.ines.isensmartcompanion.R
import kotlinx.coroutines.delay

class SplashActivity : ComponentActivity() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔊 Jouer le son au démarrage
        mediaPlayer = MediaPlayer.create(this, R.raw.startup_sound)
        mediaPlayer?.start()

        setContent {
            SplashScreen {

                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release() // Nettoyage du MediaPlayer
    }
}

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    val context = LocalContext.current
    var progress by remember { mutableStateOf(0f) }

    // 🎬 Animation de la barre de progression
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 3000)
    )

    // ⏳ Lancer le compte à rebours de 3 secondes
    LaunchedEffect(Unit) {
        delay(3000)
        onTimeout()
    }

    // 📸 Interface Splash Screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFC0CB)), // 🎀 Fond rose pastel
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 🖼️ Image de Hello Kitty
            Image(
                painter = painterResource(id = R.drawable.splash_image),
                contentDescription = "Splash Screen",
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🔄 Barre de chargement rose
            LinearProgressIndicator(
                progress = animatedProgress,
                color = Color(0xFFFF69B4), // Rose vif
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(6.dp)
                    .clip(MaterialTheme.shapes.small)
            )
        }
    }
}
