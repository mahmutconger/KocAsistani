package com.anlarsinsoftware.first_kmp_project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    val email: String = "",
    val role: String = Role.STUDENT.name, // "STUDENT" veya "COACH"

    // Öğrenciye Özel Alanlar
    val targetExam: String? = null, // Örn: "DGS"
    val coachId: String? = null,    // Bağlı olduğu koçun ID'si

    // Koça Özel Alanlar
    val connectedStudents: List<String> = emptyList() // Öğrenci ID'leri
)

enum class Role {
    STUDENT, COACH
}