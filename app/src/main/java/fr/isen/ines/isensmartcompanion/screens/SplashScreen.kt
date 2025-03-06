package fr.isen.ines.isensmartcompanion.screens

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.ines.isensmartcompanion.R
import kotlinx.coroutines.delay
import kotlin.random.Random

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
        mediaPlayer?.release()
    }
}

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    var progress by remember { mutableStateOf(0f) }

    // 🎬 Barre de chargement qui se remplit sur 3 secondes
    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
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
        // 🌸 Sakura emojis qui tombent (augmentation du nombre)
        SakuraEmojiRain(sakuraCount = 15)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // 🖼️ Image centrée, arrondie comme un logo
            Image(
                painter = painterResource(id = R.drawable.splash_image),
                contentDescription = "Splash Screen",
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape), // 🌸 Image arrondie
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 🔄 Barre de chargement qui se remplit en rose en fonction du temps
            LinearProgressIndicator(
                progress = animatedProgress,
                color = Color(0xFFFF69B4), // Rose vif
                trackColor = Color.White, // Fond blanc derrière
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(6.dp)
                    .clip(MaterialTheme.shapes.small)
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

// 🌸 Animation des fleurs de sakura (emoji) qui tombent
@Composable
fun SakuraEmojiRain(sakuraCount: Int) {
    Box(modifier = Modifier.fillMaxSize()) {
        repeat(sakuraCount) { index ->
            SakuraEmojiFallingAnimation(
                startDelay = index * 150L, // Décalage pour un effet naturel
                xPosition = Random.nextFloat()
            )
        }
    }
}

@Composable
fun SakuraEmojiFallingAnimation(startDelay: Long, xPosition: Float) {
    val fallDuration = 4000 // Temps en ms pour qu'une fleur tombe

    val infiniteTransition = rememberInfiniteTransition()
    val yOffset by infiniteTransition.animateFloat(
        initialValue = -50f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(fallDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(fallDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    LaunchedEffect(Unit) {
        delay(startDelay)
    }

    Text(
        text = "🌸",
        fontSize = 36.sp,
        modifier = Modifier
            .offset(x = (xPosition * 300).dp, y = yOffset.dp)
            .alpha(alpha)
    )
}
