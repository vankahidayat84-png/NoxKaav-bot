package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.R
import com.example.Screen
import com.example.ui.theme.DarkGrayBackground
import com.example.ui.theme.DarkerGrayIconBg
import com.example.ui.theme.LighterGray
import com.example.ui.theme.MaroonPrimary
import com.example.ui.theme.WhiteText

@Composable
fun DashboardScreen(navController: NavController) {
    val customLogoUri by com.example.UserPreferencesManager.customLogoUri.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkGrayBackground)
            .padding(top = 48.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Brand Header Section
        Box(
            modifier = Modifier
                .size(112.dp)
                .clip(CircleShape)
                .background(Color.Black)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            com.example.ui.components.NoxKaavLogo(
                logoUri = customLogoUri,
                size = 80.dp
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "NOXKAAV",
            color = WhiteText,
            fontSize = 30.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = (-1).sp
        )
        Text(
            text = "AI ASSISTANT BOT",
            color = MaroonPrimary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 4.sp,
            modifier = Modifier.offset(y = (-4).dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = buildAnnotatedString {
                append("Developed by ")
                withStyle(style = SpanStyle(color = WhiteText)) {
                    append("Maskaav")
                }
            },
            color = Color(0xFF666666),
            fontSize = 11.sp
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Main Navigation Grid
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MenuButton(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    title = "MULAI",
                    description = "Berinteraksi dengan NoxKaav",
                    icon = Icons.Filled.PlayArrow,
                    iconBgColor = MaroonPrimary,
                    onClick = { navController.navigate(Screen.ChatBot) }
                )
                MenuButton(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    title = "SETTING",
                    description = "Konfigurasi & Backup data",
                    icon = Icons.Filled.Settings,
                    iconBgColor = DarkerGrayIconBg,
                    onClick = { navController.navigate(Screen.Setting) }
                )
            }
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MenuButton(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    title = "PEMBARUAN",
                    description = "Cek changelog versi 1.0.0",
                    icon = Icons.Filled.Refresh,
                    iconBgColor = DarkerGrayIconBg,
                    onClick = { navController.navigate(Screen.Update) }
                )
                MenuButton(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    title = "TENTANG",
                    description = "Informasi pengembang",
                    icon = Icons.Filled.Info,
                    iconBgColor = DarkerGrayIconBg,
                    onClick = { navController.navigate(Screen.About) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Bottom Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable { /* interactive status */ },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaroonPrimary)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(WhiteText.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info, // Placeholder for system status icon
                        contentDescription = "Status",
                        tint = WhiteText,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Premium Access Active",
                        color = WhiteText.copy(alpha = 0.7f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "🦅 NOXKAAV ONLINE",
                        color = WhiteText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = WhiteText.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun MenuButton(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    icon: ImageVector,
    iconBgColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = LighterGray
        ),
        border = BorderStroke(1.dp, WhiteText.copy(alpha = 0.05f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = WhiteText,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Column {
                Text(
                    text = title,
                    color = WhiteText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    color = WhiteText.copy(alpha = 0.4f),
                    fontSize = 10.sp
                )
            }
        }
    }
}
