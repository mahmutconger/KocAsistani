package com.anlarsinsoftware.first_kmp_project.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.anlarsinsoftware.first_kmp_project.data.model.ScheduleDay
import com.anlarsinsoftware.first_kmp_project.data.repository.ScheduleRepository
import com.anlarsinsoftware.first_kmp_project.ui.components.toLocalDateTime
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


class StudentScheduleViewModel {
    private val repository = ScheduleRepository()
    private val scope = CoroutineScope(Dispatchers.IO)

    // Seçili Tarih (Varsayılan Bugün)
    @OptIn(ExperimentalTime::class)
    var selectedDate by mutableStateOf(
        Timestamp.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    )


        private set

    // O günün programı
    var scheduleDay by mutableStateOf<ScheduleDay?>(null)
        private set

    // Kullanıcının girdiği not
    var userNote by mutableStateOf("")
        private set

    fun onDateSelected(date: LocalDate, studentId: String) {
        selectedDate = date
        loadSchedule(studentId, date)
    }

    private fun loadSchedule(studentId: String, date: LocalDate) {
        val dateString = date.toString() // "2025-10-24"
        scope.launch {
            repository.getScheduleForDate(studentId, dateString).collect { day ->
                scheduleDay = day
                userNote = day?.studentNote ?: ""
            }
        }
    }

    fun toggleTask(studentId: String, index: Int, currentStatus: Boolean) {
        scope.launch {
            repository.updateTaskStatus(studentId, selectedDate.toString(), index, !currentStatus)
        }
    }

    fun saveNote(studentId: String) {
        scope.launch {
            repository.saveStudentNote(studentId, selectedDate.toString(), userNote)
        }
    }

    fun onNoteChange(text: String) {
        userNote = text
    }
}