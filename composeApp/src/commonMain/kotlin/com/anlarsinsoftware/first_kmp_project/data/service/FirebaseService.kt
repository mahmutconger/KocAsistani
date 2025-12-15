package com.anlarsinsoftware.first_kmp_project.data.service

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.map

//class FirebaseService {
//
//    // Firestore'a erişim noktamız
//    private val firestore = Firebase.firestore
//
//    // Veri kaydetme (INSERT/UPDATE)
//    suspend fun saveStudySession(session: StudySession) {
//        val collection = firestore.collection("study_sessions")
//
//        // DÜZELTME 1:
//        // document() parantezli ise ID ister.
//        // document (parantezsiz property) ise yeni auto-ID üretir.
//        val documentRef = if (session.id.isEmpty()) {
//            collection.document // Parantez YOK, yeni ID üretir.
//        } else {
//            collection.document(session.id) // Parantez VAR, mevcut ID'yi kullanır.
//        }
//
//        // Otomatik ID atandıysa, modeli güncelle (ID'yi içine gömüyoruz)
//        val sessionToSave = session.copy(id = documentRef.id)
//
//        // Veriyi Firestore'a yazar
//        documentRef.set(sessionToSave)
//    }
//
//    // Tüm çalışma kayıtlarını çekme (REALTIME dinleme)
//    fun getStudySessionsForStudent(studentId: String) =
//        firestore.collection("study_sessions")
//            // DÜZELTME 2:
//            // String karşılaştırma yerine Kotlin DSL yapısı kullanılıyor.
//            .where { "studentId" equalTo studentId }
//            // Snapshot'ları (anlık değişimleri) dinle
//            .snapshots
//            .map { snapshot ->
//                snapshot.documents.map { doc ->
//                    // Veriyi güvenli bir şekilde StudySession modeline çevir
//                    doc.data<StudySession>()
//                }
//            }
//}