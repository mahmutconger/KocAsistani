package com.anlarsinsoftware.first_kmp_project.data.model

import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable

@Serializable // Firebase'e yazıp okumak için bu şart!
data class Subject(
    val id: String = "", // Firestore doküman ID'si
    val name: String = "", // Örn: Matematik, Fizik
    val isActive: Boolean = true
)