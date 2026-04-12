package com.anlarsinsoftware.first_kmp_project.data.model

import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: String = "",
    val senderId: String,    // Mesajı kim attı?
    val text: String,        // Mesaj içeriği
    val timestamp: Timestamp = Timestamp.now() // Ne zaman atıldı?
)