package com.anlarsinsoftware.first_kmp_project.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.anlarsinsoftware.first_kmp_project.data.model.StudyLog
import com.anlarsinsoftware.first_kmp_project.data.repository.ExamRepository
import com.anlarsinsoftware.first_kmp_project.data.repository.StudyRepository
import com.anlarsinsoftware.first_kmp_project.ui.components.toLocalDateTime
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
// Diğer importları sildik, tam yol (full path) kullanacağız.
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

class StudentDashboardViewModel {
    private val studyRepository = StudyRepository()
    private val examRepository = ExamRepository()
    private val scope = CoroutineScope(Dispatchers.IO)

    var logs by mutableStateOf<List<StudyLog>>(emptyList())
        private set

    var daysLeft by mutableStateOf(0L)
        private set

    var examName by mutableStateOf("")
        private set

    @OptIn(ExperimentalTime::class)
    fun loadData(userId: String, targetExamId: String) {
        scope.launch {
            val config = examRepository.getExamConfig()
            val exam = config.exams.find { it.id == targetExamId }

            if (exam != null) {
                examName = exam.name

                // --- KESİN ÇÖZÜM: TAM YOL KULLANIMI ---

                // 1. 'kotlinx.datetime.Clock.System' diyerek tam adresi verdik.
                val now = Timestamp.now()

                // 2. 'kotlinx.datetime.TimeZone' diyerek tam adresi verdik.
                val timeZone = kotlinx.datetime.TimeZone.currentSystemDefault()

                // 3. Yerel saate çevir
                val localDateTime = now.toLocalDateTime(timeZone)
                val today = localDateTime.date

                val examDate = LocalDate.parse(exam.date)

                daysLeft = today.daysUntil(examDate).toLong()
            }
        }

        scope.launch {
            studyRepository.getStudyLogs(userId).collect { fetchedLogs ->
                logs = fetchedLogs
            }
        }
    }
}