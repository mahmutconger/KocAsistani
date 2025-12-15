package com.anlarsinsoftware.first_kmp_project.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.anlarsinsoftware.first_kmp_project.data.model.Role
import com.anlarsinsoftware.first_kmp_project.data.model.User
import com.anlarsinsoftware.first_kmp_project.data.remote.AuthService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

enum class AuthState {
    IDLE, LOGIN, ROLE_SELECTION, COMPLETED
}

class AuthViewModel {
    private val authService = AuthService()
    private val scope = CoroutineScope(Dispatchers.IO)

    var authState by mutableStateOf(AuthState.LOGIN)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Kullanıcı bilgisi (Role seçimi tamamlanınca burası dolar)
    var currentUser by mutableStateOf<User?>(null)
        private set

    fun onLoginOrRegister(email: String, pass: String, isRegister: Boolean) {
        if (email.isBlank() || pass.isBlank()) return

        isLoading = true
        errorMessage = null

        scope.launch {
            try {
                // 1. Firebase Auth işlemi
                val uid = if (isRegister) {
                    authService.register(email, pass)
                } else {
                    authService.login(email, pass)
                }

                // 2. Firestore Profil Kontrolü
                val existingProfile = authService.getUserProfile(uid)

                if (existingProfile != null) {
                    // Profil varsa direkt içeri al
                    currentUser = existingProfile
                    authState = AuthState.COMPLETED
                } else {
                    // Profil yoksa, yeni bir User objesi oluştur ve Rol Seçimine gönder
                    currentUser = User(id = uid, email = email)
                    authState = AuthState.ROLE_SELECTION
                }

            } catch (e: Exception) {
                errorMessage = e.message ?: "Bir hata oluştu"
            } finally {
                isLoading = false
            }
        }
    }

    fun onRoleSelected(role: Role) {
        val user = currentUser ?: return

        isLoading = true
        scope.launch {
            try {
                // Rolü güncelle
                val updatedUser = user.copy(role = role.name)
                // Firestore'a kaydet
                authService.saveUserProfile(updatedUser)

                currentUser = updatedUser
                authState = AuthState.COMPLETED
            } catch (e: Exception) {
                errorMessage = "Rol kaydedilemedi: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
    fun onExamSelected(examId: String) {
        val user = currentUser ?: return

        isLoading = true
        scope.launch {
            try {
                // User objesini güncelle
                val updatedUser = user.copy(targetExam = examId)

                // Firestore'a kaydet
                authService.saveUserProfile(updatedUser)

                // State'i güncelle
                currentUser = updatedUser
                // AuthState zaten COMPLETED'daydı, UI render olurken
                // artık targetExam dolu olduğu için Dashboard'a geçecek.
            } catch (e: Exception) {
                errorMessage = "Sınav seçimi kaydedilemedi: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}