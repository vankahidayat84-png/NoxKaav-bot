package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.DarkGrayBackground
import com.example.ui.theme.WhiteText
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToDashboard: () -> Unit
) {
    var loadingPercent by remember { mutableStateOf(0) }
    var scaleOut by remember { mutableStateOf(false) }
    
    val loadingTexts = listOf(
        "Menginisialisasi Sistem...",
        "Menyiapkan Antarmuka...",
        "Menghubungkan Modul...",
        "Memverifikasi Komponen...",
        "Menyelesaikan Proses..."
    )
    var currentTextIndex by remember { mutableStateOf(0) }

    val infiniteTransition = rememberInfiniteTransition()
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        )
    )

    val finalScale by animateFloatAsState(
        targetValue = if (scaleOut) 3f else pulse,
        animationSpec = tween(500, easing = EaseIn),
        finishedListener = {
            onNavigateToDashboard()
        }
    )

    val finalAlpha by animateFloatAsState(
        targetValue = if (scaleOut) 0f else 1f,
        animationSpec = tween(500, easing = EaseIn)
    )

    LaunchedEffect(Unit) {
        val steps = listOf(0, 3, 7, 12, 18, 25, 34, 47, 59, 71, 82, 90, 96, 99, 100)
        val totalDuration = 4000L
        val stepDuration = totalDuration / steps.size
        
        for (i in steps.indices) {
            loadingPercent = steps[i]
            currentTextIndex = (i * loadingTexts.size) / steps.size
            if (currentTextIndex >= loadingTexts.size) {
                currentTextIndex = loadingTexts.size - 1
            }
            if (i < steps.size - 1) {
                delay(stepDuration)
            }
        }
        delay(500)
        scaleOut = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkGrayBackground)
            .alpha(finalAlpha),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_bird_logo_1781089868145),
            contentDescription = "Logo NoxKaav",
            modifier = Modifier
                .size(160.dp)
                .scale(finalScale)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "NOXKAAV",
            color = WhiteText,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 4.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Memuat Aplikasi...",
            color = WhiteText.copy(alpha = 0.7f),
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = loadingTexts[currentTextIndex],
            color = WhiteText.copy(alpha = 0.8f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "$loadingPercent%",
            color = WhiteText,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
