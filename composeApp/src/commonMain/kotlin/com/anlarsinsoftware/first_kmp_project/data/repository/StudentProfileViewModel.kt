package com.anlarsinsoftware.first_kmp_project.data.repository


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.anlarsinsoftware.first_kmp_project.data.repository.StudentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

class StudentProfileViewModel {
    private val repository = StudentRepository()
    private val scope = CoroutineScope(Dispatchers.IO)

    var isLoading by mutableStateOf(false)
        private set

    var statusMessage by mutableStateOf<String?>(null)
        private set

    var isSuccess by mutableStateOf(false) // İşlem başarılı mı?
        private set

    fun linkCoach(studentId: String, code: String) {
        if (code.length < 6) {
            statusMessage = "Kod en az 6 karakter olmalı."
            return
        }

        isLoading = true
        statusMessage = null

        scope.launch {
            val result = repository.linkStudentToCoach(studentId, code)

            result.onSuccess {
                statusMessage = "Başarıyla koçunuza bağlandınız!"
                isSuccess = true
            }

            result.onFailure {
                statusMessage = "Hata: ${it.message}"
                isSuccess = false
            }

            isLoading = false
        }
    }
}