package com.anlarsinsoftware.first_kmp_project.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.anlarsinsoftware.first_kmp_project.data.model.Assignment
import com.anlarsinsoftware.first_kmp_project.data.model.DailyFocusSummary
import com.anlarsinsoftware.first_kmp_project.data.model.StudyLog
import com.anlarsinsoftware.first_kmp_project.data.repository.AssignmentRepository
import com.anlarsinsoftware.first_kmp_project.data.repository.FocusRepository
import com.anlarsinsoftware.first_kmp_project.data.repository.StudyRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

class StudentDetailViewModel {
    private val studyRepository = StudyRepository()
    private val assignmentRepository = AssignmentRepository()
    private val focusRepository = FocusRepository() // YENİ
    private val scope = CoroutineScope(Dispatchers.IO)

    // Genel Bakış Verileri
    var logs by mutableStateOf<List<StudyLog>>(emptyList())
        private set

    // Ödev Verileri
    var assignments by mutableStateOf<List<Assignment>>(emptyList())
        private set

    // Odaklanma Verileri
    var focusSummaries by mutableStateOf<List<DailyFocusSummary>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    // Tek seferde tüm verileri yükle veya tab değiştikçe yükle
    fun loadStudentData(studentId: String) {
        isLoading = true
        scope.launch {
            // 1. Çalışma Loglarını Dinle
            launch {
                studyRepository.getStudyLogs(studentId).collect {
                    logs = it
                }
            }

            // 2. Ödevleri Dinle
            launch {
                assignmentRepository.getAssignments(studentId).collect {
                    assignments = it
                }
            }

            // 3. Odaklanma Verilerini Dinle
            launch {
                focusRepository.getLast7DaysFocus(studentId).collect {
                    focusSummaries = it
                }
            }

            isLoading = false
        }
    }

    // Ödev Atama (Mevcut)
    fun assignHomework(coachId: String, studentId: String, description: String) {
        if (description.isBlank()) return
        scope.launch {
            val assignment = Assignment(
                coachId = coachId,
                studentId = studentId,
                description = description
            )
            assignmentRepository.assignHomework(assignment)
        }
    }
}