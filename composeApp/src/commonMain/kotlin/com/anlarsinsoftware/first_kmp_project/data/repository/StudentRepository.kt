package com.anlarsinsoftware.first_kmp_project.data.repository

import com.anlarsinsoftware.first_kmp_project.data.model.Invite
import com.anlarsinsoftware.first_kmp_project.data.model.User
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.firestore

class StudentRepository {
    private val firestore = Firebase.firestore

    // Öğrenciyi koça bağla
    suspend fun linkStudentToCoach(studentId: String, code: String): Result<String> {
        try {
            // 1. Kodu kontrol et
            val inviteDoc = firestore.collection("invites").document(code).get()

            if (!inviteDoc.exists) {
                return Result.failure(Exception("Geçersiz veya süresi dolmuş kod."))
            }

            val invite = inviteDoc.data<Invite>()

            // 2. Öğrenciyi güncelle (coachId ekle)
            firestore.collection("users").document(studentId)
                .update("coachId" to invite.coachId)

            // 3. Koçu güncelle (connectedStudents listesine ekle)
            // Not: arrayUnion ile listeye ekleme yapıyoruz
            firestore.collection("users").document(invite.coachId)
                .update("connectedStudents" to FieldValue.arrayUnion(studentId))

            // 4. Kodu sil (Tek kullanımlık olması için)
            // Eğer kodun çoklu kullanıma açık olmasını istersen bu satırı sil.
            firestore.collection("invites").document(code).delete()

            return Result.success("Eşleşme başarılı!")
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}