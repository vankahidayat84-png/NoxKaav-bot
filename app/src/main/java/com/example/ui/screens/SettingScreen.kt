package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ui.theme.DarkGrayBackground
import com.example.ui.theme.LighterGray
import com.example.ui.theme.MaroonPrimary
import com.example.ui.theme.WhiteText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SETTING", color = WhiteText, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = WhiteText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LighterGray
                )
            )
        },
        containerColor = DarkGrayBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingItem("Ubah Bahasa")
            SettingItem("Custom Background Chat")
            SettingItem("Pengaturan Kecerahan")
            SettingItem("Backup Data")
            SettingItem("Restore Data")
            SettingItem("Reset Data")
            SettingItem("Keluar Aplikasi", isDestructive = true)
        }
    }
}

@Composable
fun SettingItem(title: String, isDestructive: Boolean = false) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = LighterGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = if (isDestructive) MaroonPrimary else WhiteText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
