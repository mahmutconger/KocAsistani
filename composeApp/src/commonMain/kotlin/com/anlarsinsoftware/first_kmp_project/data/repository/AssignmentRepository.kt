package com.anlarsinsoftware.first_kmp_project.data.repository

import com.anlarsinsoftware.first_kmp_project.data.model.Assignment
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AssignmentRepository {
    private val firestore = Firebase.firestore

    // 1. Ödev Ata (Koç)
    suspend fun assignHomework(assignment: Assignment) {
        firestore.collection("assignments").add(assignment)
    }

    // 2. Öğrencinin Ödevlerini Getir (Canlı Dinleme)
    fun getAssignments(studentId: String): Flow<List<Assignment>> {
        return firestore.collection("assignments")
            .where { "studentId" equalTo studentId }
            .snapshots
            .map { snapshot ->
                snapshot.documents
                    .map { doc ->
                        // ID'yi doküman ID'sinden alıp objeye ekleyelim
                        val data = doc.data<Assignment>()
                        data.copy(id = doc.id)
                    }
                    .sortedByDescending { it.timestamp.seconds } // En yeni en üstte
            }
    }

    // 3. Ödevi Tamamla/Geri Al (Öğrenci)
    suspend fun toggleCompletion(assignmentId: String, isCompleted: Boolean) {
        firestore.collection("assignments").document(assignmentId)
            .update("isCompleted" to isCompleted)
    }
}