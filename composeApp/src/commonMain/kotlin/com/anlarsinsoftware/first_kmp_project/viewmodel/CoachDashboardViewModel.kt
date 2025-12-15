package com.anlarsinsoftware.first_kmp_project.viewmodel


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.anlarsinsoftware.first_kmp_project.data.model.User
import com.anlarsinsoftware.first_kmp_project.data.repository.CoachRepository
import com.anlarsinsoftware.first_kmp_project.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

class CoachDashboardViewModel {
    private val userRepository = UserRepository()
    private val coachRepository = CoachRepository()
    private val scope = CoroutineScope(Dispatchers.IO)

    var students by mutableStateOf<List<User>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var generatedCode by mutableStateOf<String?>(null)
        private set

    fun loadStudents(coachId: String) {
        isLoading = true
        scope.launch {
            // 1. Önce Koçun kendi profilini çek (Güncel öğrenci listesi için)
            val coach = userRepository.getCoachProfile(coachId)

            if (coach != null && coach.connectedStudents.isNotEmpty()) {
                // 2. Bağlı öğrencilerin detaylarını çek
                students = userRepository.getStudentsByIds(coach.connectedStudents)
            } else {
                students = emptyList()
            }
            isLoading = false
        }
    }
    fun generateCode(coachId: String) {
        isLoading = true
        scope.launch {
            try {
                val code = coachRepository.createInviteCode(coachId)
                generatedCode = code // Kodu state'e at, UI bunu görünce Dialog açacak
            } catch (e: Exception) {
                println("Kod üretme hatası: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    // YENİ: Dialog kapandığında kodu temizle
    fun dismissCodeDialog() {
        generatedCode = null
    }
}