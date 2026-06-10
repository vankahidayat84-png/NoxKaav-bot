package com.example.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Streaming
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import android.net.Uri
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val isFlickerLoader: Boolean = false,
    val isSystem: Boolean = false
)

// Gemini Data Classes
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
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

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

class ChatBotViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()
    
    private val flickerTexts = listOf(
        "🦅 NoxKaav sedang mengamati...",
        "⚙ Menjalankan perintah...",
        "📡 Menghubungkan modul...",
        "🧠 Memproses informasi...",
        "🔍 Memeriksa data...",
        "📂 Membuka arsip...",
        "☕ Sabar sebentar...",
        "✨ Menyusun respon...",
        "🔮 Membaca kemungkinan...",
        "🌙 Menjelajahi database...",
        "📖 Membuka catatan lama...",
        "🕶 Mengaktifkan mode misterius...",
        "🦉 Mencari jawaban terbaik...",
        "🎭 Menyiapkan kejutan...",
        "💭 Sedang berpikir...",
        "🚀 Menyiapkan hasil..."
    )

    fun sendMessage(text: String, imageUri: Uri? = null, isCommandRef: String = "") {
        val userMsg = ChatMessage(text = text, isUser = true)
        _messages.value = _messages.value + userMsg

        if (isCommandRef == ".rating" && imageUri != null) {
            handleRatingCommand()
            return
        }

        if (text.trim() == ".menu" || text.trim() == ".allmenu") {
            handleStaticCommand(text.trim())
        } else {
            handleAICommand(text)
        }
    }

    private fun handleRatingCommand() {
        viewModelScope.launch {
            val loaderId = UUID.randomUUID().toString()
            _messages.value = _messages.value + ChatMessage(id = loaderId, text = flickerTexts.random(), isUser = false, isFlickerLoader = true)
            
            // Flicker Animation
            val steps = 5
            for (i in 0 until steps) {
                delay(300)
                updateFlickerMessage(loaderId, flickerTexts.random())
            }
            delay(500)
            
            val responseText = """
                [HASIL RATING]
                • Kualitas Foto: 8.5/10 - Pencahayaan cukup baik.
                • Pose: Natural dan santai
                • Aura: Gelap misterius namun elegan
                • Kesan Umum: Keren dan percaya diri
                • Rating Akhir: 9/10
            """.trimIndent()
            replaceFlickerWithMessage(loaderId, responseText)
        }
    }

    private fun handleStaticCommand(command: String) {
        viewModelScope.launch {
            val loaderId = UUID.randomUUID().toString()
            _messages.value = _messages.value + ChatMessage(id = loaderId, text = flickerTexts.random(), isUser = false, isFlickerLoader = true)
            
            // Flicker Animation
            val steps = 5
            for (i in 0 until steps) {
                delay(300)
                updateFlickerMessage(loaderId, flickerTexts.random())
            }
            delay(500)
            
            val responseText = if (command == ".menu") getMenuText() else getAllMenuText()
            replaceFlickerWithMessage(loaderId, responseText)
        }
    }

    private fun handleAICommand(prompt: String) {
        viewModelScope.launch {
            val loaderId = UUID.randomUUID().toString()
            _messages.value = _messages.value + ChatMessage(id = loaderId, text = flickerTexts.random(), isUser = false, isFlickerLoader = true)
            
            // Start Flicker in parallel loop
            var isLoading = true
            val animJob = launch {
                while(isLoading) {
                    delay(300)
                    updateFlickerMessage(loaderId, flickerTexts.random())
                }
            }

            // Call API
            var responseText = "Terjadi kesalahan koneksi atau konfigurasi AI."
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = "You are NoxKaav, a mysterious AI Bot developed by Maskaav. Respond to: $prompt"))))
                )
                val response = RetrofitClient.service.generateContent(apiKey, request)
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (text != null) {
                    responseText = text
                }
            } catch (e: Exception) {
                responseText = "Error: \${e.message}"
            }
            
            isLoading = false
            animJob.cancel()
            replaceFlickerWithMessage(loaderId, responseText)
        }
    }

    private fun updateFlickerMessage(id: String, newText: String) {
        _messages.value = _messages.value.map {
            if (it.id == id) it.copy(text = newText) else it
        }
    }

    private fun replaceFlickerWithMessage(id: String, finalResult: String) {
        _messages.value = _messages.value.map {
            if (it.id == id) it.copy(text = finalResult, isFlickerLoader = false) else it
        }
    }

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
