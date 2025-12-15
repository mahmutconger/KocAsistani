package com.anlarsinsoftware.first_kmp_project.data.model

import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable

@Serializable
data class Invite(
    val code: String,
    val coachId: String,
    val timestamp: Timestamp
)