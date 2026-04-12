package com.anlarsinsoftware.first_kmp_project.data.model

import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable

@Serializable
data class Assignment(
    val id: String = "",
    val coachId: String,
    val studentId: String,
    val description: String,     // Örn: "Matematik - Türev 50 Soru"
    val isCompleted: Boolean = false,
    val timestamp: Timestamp = Timestamp.now()
)