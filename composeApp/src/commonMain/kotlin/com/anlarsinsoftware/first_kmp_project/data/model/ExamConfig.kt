package com.anlarsinsoftware.first_kmp_project.data.model

import kotlinx.serialization.Serializable

// Remote Config'den gelecek JSON'un tamamı
@Serializable
data class AppConfig(
    val exams: List<Exam> = emptyList()
)

@Serializable
data class Exam(
    val id: String,      // "DGS"
    val name: String,    // "Dikey Geçiş Sınavı"
    val date: String,    // "2024-06-30" (String tutmak daha güvenli, sonra parse ederiz)
    val lessons: List<Lesson> = emptyList()
)

@Serializable
data class Lesson(
    val id: String,      // "mat"
    val name: String,    // "Matematik"
    val topics: List<String> = emptyList() // ["Temel Kavramlar", "Üslü Sayılar"...]
)