package com.anlarsinsoftware.first_kmp_project.ui.student.focus

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.anlarsinsoftware.first_kmp_project.data.model.FocusSession
import com.anlarsinsoftware.first_kmp_project.data.repository.FocusRepository
import com.anlarsinsoftware.first_kmp_project.ui.components.toLocalDateTime
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

enum class TimerState { IDLE, RUNNING, PAUSED, BREAK }

class FocusTimerViewModel {
    private val repository = FocusRepository()
    private val scope = CoroutineScope(Dispatchers.Main)
    private var timerJob: Job? = null

    // Ayarlar
    var workDuration by mutableStateOf(25) // Dakika
    var breakDuration by mutableStateOf(5) // Dakika

    // Sayaç Durumu
    var currentState by mutableStateOf(TimerState.IDLE)
    var timeLeftSeconds by mutableStateOf(workDuration * 60)
    var currentCycle by mutableStateOf(0)

    // UI Gösterimi için (05:00 gibi)
    val formattedTime: String
        get() {
            val minutes = timeLeftSeconds / 60
            val seconds = timeLeftSeconds % 60
            return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
        }

    fun startTimer(studentId: String) {
        if (currentState == TimerState.RUNNING) return

        currentState = TimerState.RUNNING

        timerJob = scope.launch {
            while (timeLeftSeconds > 0 && currentState == TimerState.RUNNING) {
                delay(1000L) // 1 saniye bekle
                timeLeftSeconds--
            }

            if (timeLeftSeconds == 0) {
                handleTimerFinish(studentId)
            }
        }
    }

    fun pauseTimer() {
        currentState = TimerState.PAUSED
        timerJob?.cancel()
    }

    fun stopTimer() {
        currentState = TimerState.IDLE
        timerJob?.cancel()
        resetTime()
    }

    private fun handleTimerFinish(studentId: String) {
        // Süre bitti, ne yapalım?
        if (currentState == TimerState.RUNNING) { // Çalışma bitti
            // 1. Veritabanına kaydet
            saveWorkSession(studentId, workDuration)

            // 2. Molaya geç
            currentState = TimerState.BREAK
            timeLeftSeconds = breakDuration * 60
            startTimer(studentId) // Mola sayacını başlat

        } else if (currentState == TimerState.BREAK) { // Mola bitti
            // 3. Tekrar çalışmaya dön
            currentCycle++
            currentState = TimerState.IDLE // Kullanıcı başlatana kadar bekle veya otomatik başlat
            resetTime()
            // İstersen otomatik başlat: startTimer(studentId)
        }
    }

    private fun saveWorkSession(studentId: String, duration: Int) {
        scope.launch {
            val today = Timestamp.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
            val session = FocusSession(
                studentId = studentId,
                date = today,
                durationMinutes = duration,
                type = "WORK"
            )
            repository.saveSession(session)
        }
    }

    private fun resetTime() {
        timeLeftSeconds = workDuration * 60
    }

    fun updateSettings(work: Int, breakTime: Int) {
        workDuration = work
        breakDuration = breakTime
        resetTime()
    }
}