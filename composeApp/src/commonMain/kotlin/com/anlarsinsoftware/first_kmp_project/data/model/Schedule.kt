package com.anlarsinsoftware.first_kmp_project.data.model

import kotlinx.serialization.Serializable

enum class DayType {
    STUDY,      // Normal Ders Çalışma
    REST,       // Tatil
    FULL_REPEAT // Genel Tekrar
}

@Serializable
data class ScheduleDay(
    val id: String = "",         // Firestore Doc ID
    val studentId: String,
    val coachId: String? = null, // Eğer koç atadıysa doludur, öğrenci kendisi yaptıysa null
    val date: String,            // "2025-10-24" formatında (Sorgulama için en kolayı)
    val dayName: String,         // "Pazartesi" (Görsel için)
    val type: DayType = DayType.STUDY,

    // O gün yapılacaklar listesi
    val tasks: List<ScheduleTask> = emptyList(),

    // Öğrenci Girdileri
    val studentNote: String = "", // "Bugün biraz yorgundum ama..."
    val isStudentCompleted: Boolean = false, // Genel olarak günü bitirdi mi?

    // Koç Girdileri
    val coachRating: Int = 0,    // 1-5 arası yıldız
    val coachComment: String = "" // "Matematiğe biraz daha yüklen..."
)

@Serializable
data class ScheduleTask(
    val title: String,      // "Matematik - Ardışık Sayılar" veya "Paragraf Rutini"
    val targetCount: Int,   // Hedef: 50 soru
    val completedCount: Int = 0, // Öğrencinin çözdüğü
    val isDone: Boolean = false  // Tik atıldı mı?
)