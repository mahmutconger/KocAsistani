package com.anlarsinsoftware.first_kmp_project.viewmodel


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.anlarsinsoftware.first_kmp_project.data.model.User
import com.anlarsinsoftware.first_kmp_project.data.remote.AuthService
import com.anlarsinsoftware.first_kmp_project.data.repository.StudentRepository
import com.anlarsinsoftware.first_kmp_project.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

class StudentProfileViewModel {
    private val studentRepository = StudentRepository()
    private val userRepository = UserRepository() // Koç bilgisini çekmek için
    private val authService = AuthService()       // Çıkış yapmak için
    private val scope = CoroutineScope(Dispatchers.IO)

    var isLoading by mutableStateOf(false)
        private set

    var statusMessage by mutableStateOf<String?>(null)
        private set

    var isSuccess by mutableStateOf(false)

    // Koç Bilgisi State'leri
    var connectedCoach by mutableStateOf<User?>(null)
        private set

    // Profil Yükleme (Koç var mı kontrolü)
    fun loadProfileData(studentId: String) {
        scope.launch {
            // 1. Öğrencinin güncel verisini çek (coachId için)
            // (Burada UserRepository'e tekil user çekme fonksiyonu eklediğini varsayıyorum,
            // yoksa getStudentsByIds benzeri bir mantıkla çekebilirsin.
            // Şimdilik authService.getUserProfile kullanalım çünkü Faz 1'de yazmıştık)

            val student = authService.getUserProfile(studentId)

            if (student != null && !student.coachId.isNullOrBlank()) {
                // 2. Koçun bilgilerini çek
                val coach = userRepository.getCoachProfile(student.coachId)
                connectedCoach = coach
            }
        }
    }

    // Koç Bağlama (Mevcut kod)
    fun linkCoach(studentId: String, code: String) {
        if (code.length < 6) {
            statusMessage = "Kod en az 6 karakter olmalı."
            return
        }

        isLoading = true
        statusMessage = null

        scope.launch {
            val result = studentRepository.linkStudentToCoach(studentId, code)

            result.onSuccess {
                statusMessage = "Başarıyla koçunuza bağlandınız!"
                isSuccess = true
                // Bağlantı başarılı olunca profili yenile
                loadProfileData(studentId)
            }

            result.onFailure {
                statusMessage = "Hata: ${it.message}"
                isSuccess = false
            }

            isLoading = false
        }
    }

    // Çıkış Yap
    fun signOut(onComplete: () -> Unit) {
        scope.launch {
            authService.signOut()
            onComplete()
        }
    }
}