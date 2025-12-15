package com.anlarsinsoftware.first_kmp_project.data.model

import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable

@Serializable
data class StudyLog(
    val id: String = "",
    val userId: String = "",   // Kaydı giren öğrenci
    val lesson: String = "",   // Örn: Matematik
    val topic: String = "",    // Örn: Türev
    val count: Int = 0,        // Çözülen Soru
    val correct: Int = 0,
    val wrong: Int = 0,
    val timestamp: Timestamp = Timestamp.now()
)