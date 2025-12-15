package com.anlarsinsoftware.first_kmp_project.data.remote

import com.anlarsinsoftware.first_kmp_project.data.model.User
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore

class AuthService {
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    // 1. Giriş Yap
    suspend fun login(email: String, pass: String): String {
        val result = auth.signInWithEmailAndPassword(email, pass)
        return result.user?.uid ?: throw Exception("UID alınamadı")
    }

    // 2. Kayıt Ol
    suspend fun register(email: String, pass: String): String {
        val result = auth.createUserWithEmailAndPassword(email, pass)
        return result.user?.uid ?: throw Exception("Kayıt başarısız")
    }

    // 3. Profil Bilgisini Çek (Rolü var mı diye bakacağız)
    suspend fun getUserProfile(uid: String): User? {
        try {
            val doc = firestore.collection("users").document(uid).get()
            return if (doc.exists) doc.data<User>() else null
        } catch (e: Exception) {
            return null
        }
    }

    // 4. Profil Oluştur / Güncelle (Rol seçimi sonrası)
    suspend fun saveUserProfile(user: User) {
        firestore.collection("users").document(user.id).set(user)
    }

    // O anki kullanıcı ID'si
    fun getCurrentUserId(): String? = auth.currentUser?.uid

    // Çıkış yap
    suspend fun signOut() = auth.signOut()
}