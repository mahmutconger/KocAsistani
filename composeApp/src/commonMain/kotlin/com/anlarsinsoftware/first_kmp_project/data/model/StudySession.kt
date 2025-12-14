package com.anlarsinsoftware.first_kmp_project.data.model

import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable

@Serializable
data class StudySession(
    val id: String = "",
    val studentId: String = "", // Hangi öğrenciye ait
    val subjectName: String = "", // Hangi ders (Matematik)
    val topicName: String = "", // Hangi konu (Türev, Hareket)
    val solvedCount: Int = 0,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val date: Timestamp = Timestamp.now() // Kayıt tarihi
) {
    // Yardımcı hesaplama:
    val successRate: Double
        get() = if (solvedCount > 0) (correctCount.toDouble() / solvedCount.toDouble()) * 100 else 0.0
}