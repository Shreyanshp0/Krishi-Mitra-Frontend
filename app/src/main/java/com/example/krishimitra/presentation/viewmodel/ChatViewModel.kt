package com.example.krishimitra.presentation.viewmodel

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krishimitra.domain.model.ChatMessage
import com.example.krishimitra.domain.model.DomainResult
import com.example.krishimitra.domain.repository.ChatRepository
import com.example.krishimitra.data.local.SettingsManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private val Context.chatDataStore: DataStore<Preferences> by preferencesDataStore(name = "chat_prefs")

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val gson: Gson,
    @ApplicationContext private val context: Context,
    private val settingsManager: SettingsManager
) : ViewModel() {

    companion object {
        private val CHAT_HISTORY_KEY = stringPreferencesKey("chat_history")
    }

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _userLocation = MutableStateFlow<Pair<String, String>>(Pair("", "")) // state to district

    init {
        loadChatHistory()
    }

    /**
     * Set user location for context in chat requests
     */
    fun setUserLocation(state: String, district: String) {
        _userLocation.value = Pair(state, district)
    }

    /**
     * Send a message to the chatbot
     */
    fun sendMessage(text: String) {
        if (text.isBlank()) return

        // Add user message immediately
        val userMessage = ChatMessage(text, true)
        _messages.value = _messages.value + userMessage

        _isLoading.value = true

        viewModelScope.launch {
            try {
                val (state, district) = _userLocation.value
                val lang = settingsManager.languageCode.first()
                val result = chatRepository.sendMessage(text, state, district, lang)

                when (result) {
                    is DomainResult.Success -> {
                        val botMessage = ChatMessage(result.data, false)
                        _messages.value = _messages.value + botMessage
                        // Save chat history
                        saveChatHistory(_messages.value)
                    }
                    is DomainResult.Error -> {
                        val errorMessage = "Error: ${result.message}"
                        val botMessage = ChatMessage(errorMessage, false)
                        _messages.value = _messages.value + botMessage
                    }
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error sending message: ${e.message}", e)
                val offlineMessage = ChatMessage(
                    "Unable to connect. You can still view previous conversations. Try again when online.",
                    false
                )
                _messages.value = _messages.value + offlineMessage
                // Still save to history even on error
                saveChatHistory(_messages.value)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Load chat history from DataStore
     */
    private fun loadChatHistory() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    context.chatDataStore.data.collect { preferences ->
                        val json = preferences[CHAT_HISTORY_KEY] ?: "[]"
                        val type = object : TypeToken<List<ChatMessage>>() {}.type
                        val loadedMessages: List<ChatMessage> = gson.fromJson(json, type) ?: emptyList()
                        _messages.value = loadedMessages
                    }
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error loading chat history: ${e.message}", e)
                _messages.value = emptyList()
            }
        }
    }

    /**
     * Save chat history to DataStore
     */
    private suspend fun saveChatHistory(messages: List<ChatMessage>) {
        try {
            withContext(Dispatchers.IO) {
                context.chatDataStore.edit { preferences ->
                    val json = gson.toJson(messages)
                    preferences[CHAT_HISTORY_KEY] = json
                }
            }
            Log.d("ChatViewModel", "Chat history saved: ${messages.size} messages")
        } catch (e: Exception) {
            Log.e("ChatViewModel", "Error saving chat history: ${e.message}", e)
        }
    }

    /**
     * Clear all chat history
     */
    fun clearChatHistory() {
        _messages.value = emptyList()
        viewModelScope.launch {
            try {
                context.chatDataStore.edit { preferences ->
                    preferences.remove(CHAT_HISTORY_KEY)
                }
                Log.d("ChatViewModel", "Chat history cleared")
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error clearing chat history: ${e.message}", e)
            }
        }
    }
}


