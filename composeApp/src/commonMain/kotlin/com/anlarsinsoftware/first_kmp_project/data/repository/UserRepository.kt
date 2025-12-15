package com.anlarsinsoftware.first_kmp_project.data.repository

import com.anlarsinsoftware.first_kmp_project.data.model.User
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore

class UserRepository {
    private val firestore = Firebase.firestore

    // ID listesine göre öğrencilerin detaylarını getir
    // Koçun 'connectedStudents' listesindeki ID'leri alıp, User objelerine çevireceğiz.
    suspend fun getStudentsByIds(studentIds: List<String>): List<User> {
        if (studentIds.isEmpty()) return emptyList()

        // Firestore'da "whereIn" sorgusu ile toplu çekim yapıyoruz
        // Not: whereIn en fazla 10 eleman kabul eder, şimdilik basit tutalım.
        // Gerçek projede 10'arlı paketler halinde çekmek gerekir.
        return try {
            firestore.collection("users")
                .where { "id" inArray studentIds }
                .get()
                .documents
                .map { it.data() }
        } catch (e: Exception) {
            println("Öğrenci çekme hatası: ${e.message}")
            emptyList()
        }
    }

    // Koçun kendi profilini getir (Bağlı öğrenci listesini öğrenmek için)
    suspend fun getCoachProfile(coachId: String): User? {
        return try {
            firestore.collection("users").document(coachId).get().data()
        } catch (e: Exception) {
            null
        }
    }
}