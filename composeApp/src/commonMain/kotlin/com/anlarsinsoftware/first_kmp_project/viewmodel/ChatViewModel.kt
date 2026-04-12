package com.anlarsinsoftware.first_kmp_project.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.anlarsinsoftware.first_kmp_project.data.model.Message
import com.anlarsinsoftware.first_kmp_project.data.repository.ChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

class ChatViewModel {
    private val repository = ChatRepository()
    private val scope = CoroutineScope(Dispatchers.IO)

    var messages by mutableStateOf<List<Message>>(emptyList())
        private set

    var messageText by mutableStateOf("")
        private set

    // Mesajları yükle ve dinlemeye başla
    fun startListening(coachId: String, studentId: String) {
        scope.launch {
            repository.getMessages(coachId, studentId).collect { list ->
                messages = list
            }
        }
    }

    fun onTextChanged(text: String) {
        messageText = text
    }

    fun sendMessage(coachId: String, studentId: String, currentUserId: String) {
        if (messageText.isBlank()) return

        val textToSend = messageText
        messageText = "" // Kutuyu temizle

        scope.launch {
            repository.sendMessage(coachId, studentId, currentUserId, textToSend)
        }
    }
}