package com.anlarsinsoftware.first_kmp_project.data.repository

import com.anlarsinsoftware.first_kmp_project.data.model.Invite
import com.anlarsinsoftware.first_kmp_project.ui.components.toLocalDateTime
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.Timestamp
import dev.gitlive.firebase.firestore.firestore
import kotlinx.datetime.Clock

class CoachRepository {
    private val firestore = Firebase.firestore

    // Rastgele 6 haneli kod üretici (A-Z ve 0-9 arası)
    private fun generateRandomCode(length: Int = 6): String {
        val chars = ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { chars.random() }
            .joinToString("")
    }

    // Kodu üret ve Firestore'a kaydet
    suspend fun createInviteCode(coachId: String): String {
        val code = generateRandomCode()
        val invite = Invite(
            code = code,
            coachId = coachId,
            timestamp = Timestamp.now()
        )

        // "invites" koleksiyonuna, belge ID'si kod olacak şekilde kaydet
        firestore.collection("invites").document(code).set(invite)

        return code
    }
}