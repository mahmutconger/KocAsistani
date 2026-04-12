package com.anlarsinsoftware.first_kmp_project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class FocusSession(
    val id: String = "",
    val studentId: String,
    val date: String,          // "2024-12-16"
    val durationMinutes: Int,  // Örn: 45
    val type: String           // "WORK" veya "BREAK" (İstatistik için sadece WORK'ü toplarız)
)

@Serializable
data class DailyFocusSummary(
    val date: String,          // "2024-12-16"
    val totalWorkMinutes: Int, // O gün toplam kaç dk odaklanıldı?
    val sessionCount: Int      // Kaç Pomodoro bitirildi?
)