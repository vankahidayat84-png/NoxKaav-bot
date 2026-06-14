package com.example.ui.screens

import android.content.ContentValues
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

data class AudioFileInfo(
    val fileName: String,
    val fileSize: String,
    val duration: String
)

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val isFlickerLoader: Boolean = false,
    val isSystem: Boolean = false,
    val reaction: String? = null,
    val downloadProgress: Float? = null,
    val audioFiles: List<AudioFileInfo>? = null
)

@Serializable
data class GenerateContentRequest(
    val contents: List<Content>
)

@Serializable
data class Content(
    val parts: List<Part>
)

@Serializable
data class Part(
    val text: String? = null
)

@Serializable
data class GenerateContentResponse(
    val candidates: List<Candidate>? = null
)

@Serializable
data class Candidate(
    val content: Content? = null
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://googleapis.com"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    val service: GeminiApiService by lazy {
        val json = Json { ignoreUnknownKeys = true }
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
        retrofit.create(GeminiApiService::class.java)
    }
}

class ChatBotViewModel(application: android.app.Application) : androidx.lifecycle.AndroidViewModel(application) {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()
    private val context = application.applicationContext
    
    private val flickerTexts = listOf(
        "🦅 NoxKaav sedang mengamati...",
        "⚙ Menjalankan perintah...",
        "📡 Menghubungkan modul...",
        "🧠 Memproses informasi..."
    )

    private val downloadHistory = mutableListOf<AudioFileInfo>()

    init {
        sendWelcomeMessage()
    }

    private fun sendWelcomeMessage() {
        viewModelScope.launch {
            val loaderId = UUID.randomUUID().toString()
            _messages.value = listOf(ChatMessage(id = loaderId, text = "🦅 Menyiapkan Ruang Obrolan...", isUser = false, isFlickerLoader = true))
            delay(300)
            
            val welcomeText = """
                ╭──────────────────────╮
                        NOXKAAV
                ╰──────────────────────╯
                🦅 Selamat datang di NoxKaav.
                Saya adalah asisten virtual buatan Maskaav.
                
                • Ketik .menu untuk melihat menu utama
                • Ketik .download_music [link] untuk unduh lagu
                • Ketik .riwayat_download untuk cek riwayat
            """.trimIndent()
            replaceFlickerWithMessage(loaderId, welcomeText)
        }
    }

    fun clearMessages() {
        _messages.value = emptyList()
        sendWelcomeMessage()
    }

    fun toggleReaction(messageId: String, emoji: String) {
        _messages.value = _messages.value.map { msg ->
            if (msg.id == messageId) {
                if (msg.reaction == emoji) msg.copy(reaction = null) else msg.copy(reaction = emoji)
            } else msg
        }
    }

    fun sendMessage(text: String, imageUri: Uri? = null, isCommandRef: String = "") {
        val userMsg = ChatMessage(text = text, isUser = true)
        _messages.value = _messages.value + userMsg

        val trimmedText = text.trim()
        when {
            trimmedText == ".menu" || trimmedText == ".allmenu" -> handleStaticCommand(trimmedText)
            trimmedText.startsWith(".download_music") -> handleDownloadMusic(trimmedText)
            trimmedText == ".riwayat_download" -> handleDownloadHistory()
            else -> handleAICommand(text)
        }
    }

    private fun handleDownloadMusic(command: String) {
        val url = command.removePrefix(".download_music").trim()
        if (url.isEmpty()) {
            _messages.value = _messages.value + ChatMessage(text = "Harap masukkan URL musik.\nContoh: .download_music https://youtu.be...", isUser = false)
            return
        }
        
        viewModelScope.launch {
            val loaderId = UUID.randomUUID().toString()
            _messages.value = _messages.value + ChatMessage(id = loaderId, text = "📥 Menghubungkan ke API Pengunduh...", isUser = false, isFlickerLoader = true, downloadProgress = 0.1f)
            
            val fileName = "NoxKaav_${System.currentTimeMillis()}.mp3"
            
            val success = withContext(Dispatchers.IO) {
                try {
                    val encodedUrl = URLEncoder.encode(url, "UTF-8")
                    val downloadApiUrl = "https://vexdl.com"
                    
                    val client = OkHttpClient()
                    val request = Request.Builder().url(downloadApiUrl).build()
                    val response = client.newCall(request).execute()
                    
                    if (response.isSuccessful && response.body != null) {
                        val body = response.body!!
                        val totalBytes = body.contentLength()
                        
                        val resolver = context.contentResolver
                        val contentValues = ContentValues().apply {
                            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                            put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3")
                            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MUSIC + "/NoxKaav")
                        }
                        
                        val audioUri = resolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues)
                        if (audioUri != null) {
                            resolver.openOutputStream(audioUri).use { outputStream ->
                                val inputStream = body.byteStream()
                                val buffer = ByteArray(4096)
                                var bytesRead: Int
                                var downloadedBytes = 0L
                                
                                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                                    outputStream?.write(buffer, 0, bytesRead)
                                    downloadedBytes += bytesRead
                                    
                                    if (totalBytes > 0) {
                                        val progress = downloadedBytes.toFloat() / totalBytes.toFloat()
                                        updateFlickerMessage(loaderId, "📥 Mengunduh data lagu... ${(progress * 100).toInt()}%", progress)
                                    }
                                }
                                outputStream?.flush()
                            }
                            
                            if (totalBytes > 0) {
                                val sizeInMb = String.format(Locale.US, "%.2f MB", totalBytes.toFloat() / (1024 * 1024))
                                downloadHistory.add(AudioFileInfo(fileName, sizeInMb, "Unknown"))
                            }
                            true
                        } else {
                            false
                        }
                    } else {
                        false
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }
            
            if (success) {
                replaceFlickerWithMessage(loaderId, "✅ Lagu Berhasil Disimpan!\n\n📁 Folder: /Music/NoxKaav/\n🎵 File: $fileName")
            } else {
                replaceFlickerWithMessage(loaderId, "❌ Gagal mengunduh file.\n\nAlasan:\nKoneksi API terputus atau URL tidak didukung sistem.")
            }
        }
    }


    private fun handleDownloadHistory() {
        val report = if (downloadHistory.isEmpty()) {
            "📂 Riwayat Download kosong."
        } else {
            "📁 Riwayat Unduhan Musik:\n" + downloadHistory.joinToString("\n") { "• ${it.fileName} (${it.fileSize})" }
        }
        _messages.value = _messages.value + ChatMessage(text = report, isUser = false)
    }
messages.value = _messages.value + ChatMessage(text = "Fitur menu sedang disiapkan.", isUser = false)}private fun handleAICommand(text: String) {viewModelScope.launch {val loaderId = UUID.randomUUID().toString()_messages.value = _messages.value + ChatMessage(id = loaderId, text = "🧠 Memproses data...", isUser = false, isFlickerLoader = true)delay(500)try {val req = GenerateContentRequest(contents = listOf(Content(parts = listOf(Part(text = text)))))val res = RetrofitClient.service.generateContent(BuildConfig.GEMINI_API_KEY, req)val reply = res.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "Tidak ada respon dari sistem."replaceFlickerWithMessage(loaderId, reply)} catch (e: Exception) {replaceFlickerWithMessage(loaderId, "Terjadi kesalahan jaringan.")}}}private fun updateFlickerMessage(id: String, newText: String, progress: Float? = null) {_messages.value = _messages.value.map { if (it.id == id) it.copy(text = newText, downloadProgress = progress) else it }}private fun replaceFlickerWithMessage(id: String, finalText: String) {_messages.value = _messages.value.map { if (it.id == id) it.copy(text = finalText, isFlickerLoader = false, downloadProgress = null) else it }}private fun handleRatingCommand() {}private fun handleOpenFolder() {}private fun handleStorageCommand() {}}
    private fun handleStaticCommand(cmd: String) {


    private fun replaceFlickerWithMessage(id: String, finalResult: String) {
        _messages.value = _messages.value.map {
            if (it.id == id) it.copy(text = finalResult, isFlickerLo
    private fun getMenuText(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val date = dateFormat.format(Date())
        val time = timeFormat.format(Date())

        return """
            ╭──────────────────────╮
                     NOXKAAV
            ╰──────────────────────╯

            ◈ BOT NAME    : NoxKaav
            ◈ OWNER       : Maskaav
            ◈ USER STATUS : Premium
            ◈ DATE        : $date
            ◈ TIME        : $time
            ◈ BATTERY     : 100%
            ◈ CONNECTION  : Terhubung
            ◈ VERSION     : 1.0.0

            ═══════════════════════

            ███ ███ ███ ███ ███
            ███ ███ ███ ███ ███

            ═══════════════════════

            🦅 "Darkness Watches."

            Powered By Maskaav

            ╭── MENU UTAMA ──╮

            • .allmenu
            • .tools
            • .ai
            • .network
            • .converter
            • .generator
            • .setting
            • .about

            ╰────────────────╯

            💡 Motivasi Hari Ini

            "Kesuksesan bukan tentang seberapa cepat kamu sampai,
            tetapi seberapa konsisten kamu melangkah meski perlahan."

            Terima kasih telah menggunakan NoxKaav.
            - Maskaav
        """.trimIndent()
    }

    private fun getAllMenuText(): String {
        return """
            ╭──────────────────────╮
                     NOXKAAV
            ╰──────────────────────╯
            
            ◈ BOT NAME    : NoxKaav
            ◈ OWNER       : Maskaav
            ◈ USER STATUS : Premium
            ◈ CONNECTION  : Terhubung
            ◈ VERSION     : 1.0.0

            ═══════════════════════

            🦅 "Darkness Watches."

            Powered By Maskaav

            ═══════════════════════

            ╭── KEMBALI KE MENU UTAMA ──╮
            • .back
            ╰──────────────────────────╯

            ╭── AI MENU ──╮
            • .ask
            • .chat
            • .translate
            • .ringkas
            • .kode
            ╰─────────────╯

            ╭── TOOLS MENU ──╮
            • .catatan
            • .kalkulator
            • .qr
            • .password
            • .uuid
            ╰────────────────╯

            ╭── HIBURAN MENU ──╮
            • .puisi
            • .cerita_rakyat
            • .lagu
            • .phonk
            • .pantun
            • .quotes
            • .tebak_kata
            ╰──────────────────╯

            ╭── MEDIA MENU ──╮
            • .download_music
            • .riwayat_download
            • .folder_music
            ╰────────────────╯

            ╭── RANDOM FUN ──╮
            • .cek_pacar
            • .cek_jomblo
            • .cek_jodoh
            • .cek_dompet
            • .cek_hoki
            • .cek_sultan
            • .cek_ganteng
            • .cek_cantik
            • .cek_wibu
            • .cek_mager
            • .rating
            ╰────────────────╯

            ╭── INFORMASI ──╮
            • .tanggal
            • .jam
            • .kalender
            • .cuaca
            ╰────────────────╯

            ═══════════════════════

            💡 Motivasi Hari Ini

            "Jangan takut berjalan lambat,
            takutlah jika kamu berhenti melangkah."

            🙏 Terima kasih telah menggunakan NoxKaav.
            Dukungan dan masukan Anda membantu aplikasi ini terus berkembang.

            - Maskaav

            ═══════════════════════
        """.trimIndent()
    }
}
