package com.anlarsinsoftware.first_kmp_project.data.repository

import com.anlarsinsoftware.first_kmp_project.data.model.StudyLog
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.Timestamp
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StudyRepository {
    private val firestore = Firebase.firestore

    // Yeni çalışma kaydet
    suspend fun addStudyLog(log: StudyLog) {
        firestore.collection("studies").add(log)
    }

    // Öğrencinin tüm kayıtlarını getir (Tarihe göre sıralı)
    fun getStudyLogs(userId: String): Flow<List<StudyLog>> {
        return firestore.collection("studies")
            .where { "userId" equalTo userId }
            .snapshots
            .map { snapshot ->
                snapshot.documents
                    .map { it.data<StudyLog>() }
                    .sortedByDescending { it.timestamp.seconds }
            }
    }
}