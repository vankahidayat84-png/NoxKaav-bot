package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.UserPreferencesManager
import com.example.ui.theme.DarkGrayBackground
import com.example.ui.theme.LighterGray
import com.example.ui.theme.MaroonPrimary
import com.example.ui.theme.WhiteText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    navController: NavController,
    chatViewModel: ChatBotViewModel = viewModel()
) {
    val context = LocalContext.current
    val activity = LocalContext.current as? Activity
    val profileImageUri by UserPreferencesManager.profileImageUri.collectAsState()
    val chatBackgroundUri by UserPreferencesManager.chatBackgroundUri.collectAsState()
    val userName by UserPreferencesManager.userName.collectAsState()
    val brightness by UserPreferencesManager.brightness.collectAsState()

    var showProfileDialog by remember { mutableStateOf(false) }
    var showBrightnessDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }
    
    val profileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            UserPreferencesManager.setProfileImageUri(uri.toString())
        }
    }
    
    val backgroundLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            UserPreferencesManager.setChatBackgroundUri(uri.toString())
        }
    }

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
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Section
            Card(
                modifier = Modifier.fillMaxWidth().clickable { showProfileDialog = true },
                colors = CardDefaults.cardColors(containerColor = LighterGray)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (profileImageUri != null) {
                        AsyncImage(
                            model = profileImageUri,
                            contentDescription = "User Profile",
                            modifier = Modifier.size(50.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Default User Profile",
                            tint = WhiteText,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(DarkGrayBackground)
                                .padding(8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = userName, color = WhiteText, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(text = "Ketuk untuk edit profil", color = WhiteText.copy(alpha = 0.5f), fontSize = 12.sp)
                    }
                }
            }

            Text("PENGATURAN UMUM", color = MaroonPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            
            SettingItem("Custom Background Chat") {
                backgroundLauncher.launch(androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            if (chatBackgroundUri != null) {
                SettingItem("Hapus Background Chat") {
                    UserPreferencesManager.setChatBackgroundUri(null)
                    Toast.makeText(context, "Background dihapus", Toast.LENGTH_SHORT).show()
                }
            }
            
            SettingItem("Pengaturan Kecerahan") { showBrightnessDialog = true }
            
            Text("DATA", color = MaroonPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            SettingItem("Backup Data") {
                Toast.makeText(context, "Data berhasil diexport ke JSON (Simulasi)", Toast.LENGTH_SHORT).show()
            }
            SettingItem("Restore Data") {
                Toast.makeText(context, "Data berhasil diimport (Simulasi)", Toast.LENGTH_SHORT).show()
            }
            SettingItem("Reset Data", isDestructive = true) { showResetDialog = true }
            
            Text("SISTEM", color = MaroonPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            SettingItem("Keluar Aplikasi", isDestructive = true) { showExitDialog = true }
        }
    }

    if (showProfileDialog) {
        var tempName by remember { mutableStateOf(userName) }
        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            title = { Text("Profil Pengguna", color = WhiteText) },
            text = {
                Column {
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        label = { Text("Nama", color = WhiteText) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = WhiteText, unfocusedTextColor = WhiteText
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { 
                        profileLauncher.launch(androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }, colors = ButtonDefaults.buttonColors(containerColor = MaroonPrimary)) {
                        Text("Ganti Foto Profil", color = WhiteText)
                    }
                    if (profileImageUri != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { UserPreferencesManager.setProfileImageUri(null) }) {
                            Text("Hapus Foto", color = MaroonPrimary)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    UserPreferencesManager.setUserName(tempName)
                    showProfileDialog = false
                }) { Text("Simpan", color = WhiteText) }
            },
            dismissButton = {
                TextButton(onClick = { showProfileDialog = false }) { Text("Batal", color = WhiteText) }
            },
            containerColor = LighterGray
        )
    }

    if (showBrightnessDialog) {
        var tempBrightness by remember { mutableFloatStateOf(brightness) }
        AlertDialog(
            onDismissRequest = { showBrightnessDialog = false },
            title = { Text("Kecerahan", color = WhiteText) },
            text = {
                Slider(
                    value = tempBrightness,
                    onValueChange = { tempBrightness = it },
                    valueRange = 0.1f..1.0f,
                    colors = SliderDefaults.colors(thumbColor = MaroonPrimary, activeTrackColor = MaroonPrimary)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    UserPreferencesManager.setBrightness(tempBrightness)
                    showBrightnessDialog = false
                }) { Text("Terapkan", color = WhiteText) }
            },
            containerColor = LighterGray
        )
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Data", color = MaroonPrimary) },
            text = { Text("Apakah Anda yakin ingin menghapus seluruh data dan riwayat chat? Tindakan ini tidak dapat dibatalkan.", color = WhiteText) },
            confirmButton = {
                TextButton(onClick = {
                    UserPreferencesManager.clearAll()
                    chatViewModel.clearMessages()
                    showResetDialog = false
                    Toast.makeText(context, "Data direset", Toast.LENGTH_SHORT).show()
                }) { Text("Hapus Semua", color = MaroonPrimary) }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text("Batal", color = WhiteText) }
            },
            containerColor = LighterGray
        )
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Keluar", color = WhiteText) },
            text = { Text("Apakah Anda yakin ingin keluar dari NoxKaav?", color = WhiteText) },
            confirmButton = {
                TextButton(onClick = {
                    activity?.finish()
                }) { Text("Keluar", color = MaroonPrimary) }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) { Text("Batal", color = WhiteText) }
            },
            containerColor = LighterGray
        )
    }
}

@Composable
fun SettingItem(title: String, isDestructive: Boolean = false, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = LighterGray)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
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
