package com.anlarsinsoftware.first_kmp_project.data.repository

import com.anlarsinsoftware.first_kmp_project.data.model.DailyFocusSummary
import com.anlarsinsoftware.first_kmp_project.data.model.FocusSession
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.*

class FocusRepository {
    private val firestore = Firebase.firestore

    // 1. Oturum Bittiğinde Kaydet (Firestore Transaction ile güvenli artırma yapılabilir ama şimdilik basit tutalım)
    suspend fun saveSession(session: FocusSession) {
        // Önce Session kaydı atalım (Detaylı log için)
        firestore.collection("focus_sessions").add(session)

        // Sonra o günün özetini güncelleyelim
        val summaryId = "${session.studentId}_${session.date}"
        val summaryRef = firestore.collection("focus_summaries").document(summaryId)

        try {
            val snapshot = summaryRef.get()
            if (snapshot.exists) {
                val current = snapshot.data<DailyFocusSummary>()
                val updated = current.copy(
                    totalWorkMinutes = current.totalWorkMinutes + session.durationMinutes,
                    sessionCount = current.sessionCount + 1
                )
                summaryRef.set(updated)
            } else {
                val newSummary = DailyFocusSummary(
                    date = session.date,
                    totalWorkMinutes = session.durationMinutes,
                    sessionCount = 1
                )
                summaryRef.set(newSummary)
            }
        } catch (e: Exception) {
            println("Focus kayıt hatası: ${e.message}")
        }
    }

    // 2. Son 7 Günün Verisini Getir (Grafik İçin)
    fun getLast7DaysFocus(studentId: String): Flow<List<DailyFocusSummary>> {
        // Not: Firestore'da tarih filtresi yapmak gerekebilir, şimdilik tümünü çekip client'ta filtreleyelim (MVP için)
        // Gerçekte: .where("date" >= "7 gün önce") kullanılmalı.
        return firestore.collection("focus_summaries")
            .where { "studentId" equalTo studentId } // Not: Document ID pattern kullandığımız için bu sorgu değişebilir, koleksiyon yapısına dikkat.
            // Basitlik adına: studentId alanını DailyFocusSummary içine de ekleyip sorgulayabiliriz.
            // Şimdilik collection path'i 'users/{id}/focus_summaries' yapmak daha mantıklı olurdu ama
            // global collection üzerinden gidelim:
            .snapshots
            .map { snapshot ->
                snapshot.documents
                    .map { it.data<DailyFocusSummary>() }
                    .takeLast(7) // Son 7'yi al (Tabii sıralama gerekir)
            }
    }
}