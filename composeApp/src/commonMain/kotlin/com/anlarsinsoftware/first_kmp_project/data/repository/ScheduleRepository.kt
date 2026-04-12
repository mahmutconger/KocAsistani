package com.anlarsinsoftware.first_kmp_project.data.repository

import com.anlarsinsoftware.first_kmp_project.data.model.ScheduleDay
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ScheduleRepository {
    private val firestore = Firebase.firestore

    // Belge ID'si oluşturucu
    private fun getDocId(studentId: String, date: String): String {
        return "${studentId}_${date}"
    }

    // 1. O günün programını getir (Canlı Dinleme)
    fun getScheduleForDate(studentId: String, date: String): Flow<ScheduleDay?> {
        val docId = getDocId(studentId, date)
        return firestore.collection("schedules").document(docId).snapshots
            .map { snapshot ->
                if (snapshot.exists) snapshot.data<ScheduleDay>() else null
            }
    }

    // 2. Öğrenci: Görev tamamla (Tik at)
    suspend fun updateTaskStatus(studentId: String, date: String, taskIndex: Int, isDone: Boolean) {
        val docId = getDocId(studentId, date)

        // Firestore'da liste içindeki spesifik bir objeyi güncellemek zordur.
        // Bu yüzden önce okuyup, listeyi güncelleyip tekrar yazacağız (Transaction en doğrusudur ama şimdilik düz yapalım)
        val docRef = firestore.collection("schedules").document(docId)
        val snapshot = docRef.get()

        if (snapshot.exists) {
            val schedule = snapshot.data<ScheduleDay>()
            // Listeyi kopyala ve ilgili elemanı değiştir
            val updatedTasks = schedule.tasks.toMutableList().apply {
                this[taskIndex] = this[taskIndex].copy(isDone = isDone)
            }
            docRef.update("tasks" to updatedTasks)
        }
    }

    // 3. Öğrenci: Günlük notunu kaydet
    suspend fun saveStudentNote(studentId: String, date: String, note: String) {
        val docId = getDocId(studentId, date)
        firestore.collection("schedules").document(docId).update("studentNote" to note)
    }

    suspend fun saveSchedule(schedule: ScheduleDay) {
        val docId = getDocId(schedule.studentId, schedule.date)
        // set() kullanıyoruz çünkü var olanın üzerine yazsın istiyoruz
        firestore.collection("schedules").document(docId).set(schedule)
    }
}