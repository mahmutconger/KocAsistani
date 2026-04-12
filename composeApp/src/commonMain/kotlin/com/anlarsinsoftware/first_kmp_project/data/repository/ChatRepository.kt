package com.anlarsinsoftware.first_kmp_project.data.repository

import com.anlarsinsoftware.first_kmp_project.data.model.Message
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatRepository {
    private val firestore = Firebase.firestore

    // Sohbet Odası ID'si oluşturucu (Standart olması için)
    // Her zaman "coachId_studentId" formatında olacak
    private fun getChatRoomId(coachId: String, studentId: String): String {
        return "${coachId}_${studentId}"
    }

    // Mesaj Gönder
    suspend fun sendMessage(coachId: String, studentId: String, senderId: String, text: String) {
        val roomId = getChatRoomId(coachId, studentId)
        val message = Message(
            senderId = senderId,
            text = text
        )
        // chats -> roomId -> messages koleksiyonuna ekle
        firestore.collection("chats").document(roomId).collection("messages").add(message)
    }

    // Canlı Mesajları Dinle (Flow ile)
    fun getMessages(coachId: String, studentId: String): Flow<List<Message>> {
        val roomId = getChatRoomId(coachId, studentId)

        return firestore.collection("chats").document(roomId)
            .collection("messages")
            .orderBy("timestamp", Direction.ASCENDING) // Eskiden yeniye sırala
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { it.data() }
            }
    }
}