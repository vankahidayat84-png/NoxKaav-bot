package com.example

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object UserPreferencesManager {
    private const val PREFS_NAME = "noxkaav_prefs"
    private lateinit var prefs: SharedPreferences

    private val _userName = MutableStateFlow("User")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _profileImageUri = MutableStateFlow<String?>(null)
    val profileImageUri: StateFlow<String?> = _profileImageUri.asStateFlow()

    private val _chatBackgroundUri = MutableStateFlow<String?>(null)
    val chatBackgroundUri: StateFlow<String?> = _chatBackgroundUri.asStateFlow()

    private val _language = MutableStateFlow("id")
    val language: StateFlow<String> = _language.asStateFlow()

    private val _brightness = MutableStateFlow(1.0f)
    val brightness: StateFlow<Float> = _brightness.asStateFlow()

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _userName.value = prefs.getString("user_name", "User") ?: "User"
        _profileImageUri.value = prefs.getString("profile_image_uri", null)
        _chatBackgroundUri.value = prefs.getString("chat_background_uri", null)
        _language.value = prefs.getString("language", "id") ?: "id"
        _brightness.value = prefs.getFloat("brightness", 1.0f)
    }

    fun setUserName(name: String) {
        _userName.value = name
        prefs.edit().putString("user_name", name).apply()
    }

    fun setProfileImageUri(uri: String?) {
        _profileImageUri.value = uri
        prefs.edit().putString("profile_image_uri", uri).apply()
    }

    fun setChatBackgroundUri(uri: String?) {
        _chatBackgroundUri.value = uri
        prefs.edit().putString("chat_background_uri", uri).apply()
    }

    fun setLanguage(lang: String) {
        _language.value = lang
        prefs.edit().putString("language", lang).apply()
    }

    fun setBrightness(level: Float) {
        _brightness.value = level
        prefs.edit().putFloat("brightness", level).apply()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
        _userName.value = "User"
        _profileImageUri.value = null
        _chatBackgroundUri.value = null
        _language.value = "id"
        _brightness.value = 1.0f
    }
}
