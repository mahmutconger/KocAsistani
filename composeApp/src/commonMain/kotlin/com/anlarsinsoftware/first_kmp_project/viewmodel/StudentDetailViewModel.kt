package com.anlarsinsoftware.first_kmp_project.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.anlarsinsoftware.first_kmp_project.data.model.StudyLog
import com.anlarsinsoftware.first_kmp_project.data.repository.StudyRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

class StudentDetailViewModel {
    private val repository = StudyRepository()
    private val scope = CoroutineScope(Dispatchers.IO)

    var logs by mutableStateOf<List<StudyLog>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun loadStudentLogs(studentId: String) {
        isLoading = true
        scope.launch {
            repository.getStudyLogs(studentId).collect { fetchedLogs ->
                logs = fetchedLogs
                isLoading = false
            }
        }
    }
}