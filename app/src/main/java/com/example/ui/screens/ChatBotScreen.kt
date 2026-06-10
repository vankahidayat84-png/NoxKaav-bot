package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.Add
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.Person
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.R
import com.example.ui.theme.DarkGrayBackground
import com.example.ui.theme.LighterGray
import com.example.ui.theme.MaroonPrimary
import com.example.ui.theme.WhiteText
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBotScreen(
    navController: NavController,
    viewModel: ChatBotViewModel = viewModel()
) {
    val messages by viewModel.messages.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> 
            if (uri != null) {
                selectedImageUri = uri
                viewModel.sendMessage("📸 Mengirim foto...", imageUri = uri, isCommandRef = ".rating")
            }
        }
    )

    val profileImageUri by com.example.UserPreferencesManager.profileImageUri.collectAsState()
    val chatBackgroundUri by com.example.UserPreferencesManager.chatBackgroundUri.collectAsState()
    val customLogoUri by com.example.UserPreferencesManager.customLogoUri.collectAsState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        com.example.ui.components.NoxKaavLogo(
                            logoUri = customLogoUri,
                            size = 32.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("NOXKAAV BOT", color = WhiteText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("🟢 Online", color = Color(0xFF00FF00), fontSize = 12.sp)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = WhiteText)
                    }
                },
                actions = {
                    if (profileImageUri != null) {
                        coil.compose.AsyncImage(
                            model = profileImageUri,
                            contentDescription = "User Profile",
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(32.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Filled.Person,
                            contentDescription = "Default User Profile",
                            tint = WhiteText,
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(32.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(com.example.ui.theme.LighterGray)
                                .padding(4.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LighterGray
                )
            )
        },
        containerColor = DarkGrayBackground,
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(WindowInsets.navigationBars.asPaddingValues())
            ) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val quickCommands = listOf(".menu", ".allmenu", ".about")
                    items(quickCommands) { cmd ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = LighterGray,
                            modifier = Modifier.clickable {
                                viewModel.sendMessage(cmd)
                            }
                        ) {
                            Text(
                                text = cmd,
                                color = WhiteText,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Ketik perintah atau pesan...", color = WhiteText.copy(alpha = 0.5f)) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    leadingIcon = {
                        IconButton(onClick = {
                            photoPickerLauncher.launch(
                                androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }) {
                            Icon(Icons.Filled.Add, contentDescription = "Attach", tint = WhiteText)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = LighterGray,
                        unfocusedContainerColor = LighterGray,
                        focusedBorderColor = MaroonPrimary,
                        unfocusedBorderColor = LighterGray,
                        focusedTextColor = WhiteText,
                        unfocusedTextColor = WhiteText
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                FloatingActionButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            viewModel.sendMessage(inputText)
                            inputText = ""
                        }
                    },
                    containerColor = MaroonPrimary,
                    contentColor = WhiteText
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                }
                } // close Row
            } // close Column
        } // close bottomBar
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (chatBackgroundUri != null) {
                coil.compose.AsyncImage(
                    model = chatBackgroundUri,
                    contentDescription = "Chat Background",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(messages) { message ->
                    ChatBubble(
                        message = message,
                        profileImageUri = profileImageUri,
                        botLogoUri = customLogoUri,
                        onReactionSelected = { emoji ->
                            viewModel.toggleReaction(message.id, emoji)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: ChatMessage,
    profileImageUri: String?,
    botLogoUri: String?,
    onReactionSelected: (String) -> Unit
) {
    val alignment = if (message.isUser) Arrangement.End else Arrangement.Start
    val bgColor = if (message.isUser) MaroonPrimary else LighterGray
    val shape = if (message.isUser) {
        RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)
    }

    var showReactionMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = alignment,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!message.isUser) {
            com.example.ui.components.NoxKaavLogo(
                logoUri = botLogoUri,
                size = 28.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start) {
            Box(
                modifier = Modifier
                    .clip(shape)
                    .background(bgColor)
                    .pointerInput(message.isFlickerLoader) {
                        detectTapGestures(
                            onLongPress = { 
                                if (!message.isFlickerLoader && !message.isUser) showReactionMenu = true 
                            }
                        )
                    }
                    .padding(12.dp)
                    .widthIn(max = 240.dp)
            ) {
                Column {
                    Text(
                        text = message.text,
                        color = WhiteText,
                        fontSize = 14.sp
                    )
                    
                    if (message.downloadProgress != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val animatedProgress by androidx.compose.animation.core.animateFloatAsState(
                                targetValue = message.downloadProgress,
                                animationSpec = androidx.compose.animation.core.tween(durationMillis = 300)
                            )
                            LinearProgressIndicator(
                                progress = { animatedProgress },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(4.dp)
                                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(2.dp)),
                                color = com.example.ui.theme.MaroonPrimary,
                                trackColor = com.example.ui.theme.DarkerGrayIconBg
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${(animatedProgress * 100).toInt()}%",
                                color = WhiteText,
                                fontSize = 10.sp
                            )
                        }
                    }

                    if (message.audioFiles != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        message.audioFiles.forEach { audioInfo ->
                            AudioPlayerItem(audioInfo = audioInfo)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            if (message.reaction != null) {
                Box(
                    modifier = Modifier
                        .offset(y = (-8).dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(com.example.ui.theme.DarkerGrayIconBg)
                        .padding(4.dp)
                ) {
                    Text(text = message.reaction, fontSize = 14.sp)
                }
            }
            
            if (showReactionMenu) {
                DropdownMenu(
                    expanded = showReactionMenu,
                    onDismissRequest = { showReactionMenu = false },
                    modifier = Modifier.background(com.example.ui.theme.DarkerGrayIconBg)
                ) {
                    val emojis = listOf("👍", "❤️", "😂", "😮", "😢", "😡")
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        emojis.forEach { emoji ->
                            Text(
                                text = emoji,
                                fontSize = 24.sp,
                                modifier = Modifier.clickable {
                                    onReactionSelected(emoji)
                                    showReactionMenu = false
                                }
                            )
                        }
                    }
                    val currentContext = androidx.compose.ui.platform.LocalContext.current
                    DropdownMenuItem(
                        text = { Text("Salin Teks", color = WhiteText) },
                        onClick = {
                            val clipboard = currentContext.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                            val clip = android.content.ClipData.newPlainText("Pesan NoxKaav", message.text)
                            clipboard.setPrimaryClip(clip)
                            android.widget.Toast.makeText(currentContext, "Teks berhasil disalin", android.widget.Toast.LENGTH_SHORT).show()
                            showReactionMenu = false
                        }
                    )
                }
            }
        }

        if (message.isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            if (profileImageUri != null) {
                coil.compose.AsyncImage(
                    model = profileImageUri,
                    contentDescription = "User Avatar",
                    modifier = Modifier
                        .size(28.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "User Avatar",
                    tint = WhiteText,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(LighterGray)
                        .padding(4.dp)
                )
            }
        }
    }
}

@Composable
fun AudioPlayerItem(audioInfo: AudioFileInfo) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    val mediaPlayer = remember { android.media.MediaPlayer() }

    DisposableEffect(Unit) {
        onDispose {
            if (isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.release()
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(com.example.ui.theme.DarkerGrayIconBg)
            .padding(8.dp)
    ) {
        IconButton(
            onClick = {
                if (isPlaying) {
                    mediaPlayer.stop()
                    mediaPlayer.reset()
                    isPlaying = false
                } else {
                    try {
                        val uri = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION)
                        mediaPlayer.setDataSource(context, uri)
                        mediaPlayer.prepare()
                        mediaPlayer.start()
                        isPlaying = true
                        mediaPlayer.setOnCompletionListener {
                            isPlaying = false
                        }
                    } catch (e: Exception) {
                        isPlaying = false
                        android.widget.Toast.makeText(context, "Gagal memutar audio", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.size(32.dp).background(LighterGray, shape = androidx.compose.foundation.shape.CircleShape)
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Filled.Close else Icons.Filled.PlayArrow,
                contentDescription = if (isPlaying) "Stop" else "Play",
                tint = WhiteText,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = audioInfo.fileName,
                color = WhiteText,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = audioInfo.fileSize,
                    color = WhiteText.copy(alpha = 0.7f),
                    fontSize = 10.sp
                )
                Text(
                    text = audioInfo.duration,
                    color = WhiteText.copy(alpha = 0.7f),
                    fontSize = 10.sp
                )
            }
        }
    }
}
