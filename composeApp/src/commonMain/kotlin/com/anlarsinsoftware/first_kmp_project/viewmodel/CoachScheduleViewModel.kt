package com.anlarsinsoftware.first_kmp_project.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.anlarsinsoftware.first_kmp_project.data.model.DayType
import com.anlarsinsoftware.first_kmp_project.data.model.Lesson
import com.anlarsinsoftware.first_kmp_project.data.model.ScheduleDay
import com.anlarsinsoftware.first_kmp_project.data.model.ScheduleTask
import com.anlarsinsoftware.first_kmp_project.data.repository.ExamRepository
import com.anlarsinsoftware.first_kmp_project.data.repository.ScheduleRepository
import com.anlarsinsoftware.first_kmp_project.ui.components.toLocalDateTime
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.datetime.*

class CoachScheduleViewModel {
    private val scheduleRepository = ScheduleRepository()
    private val examRepository = ExamRepository()
    private val scope = CoroutineScope(Dispatchers.IO)

    // --- FORM ALANLARI ---
    var selectedDate by mutableStateOf(
        Timestamp.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date.plus(DatePeriod(days = 1))
    )
    var selectedDayType by mutableStateOf(DayType.STUDY)

    // Görev Listesi
    var tasks = mutableStateListOf<ScheduleTask>()
        private set

    // --- REMOTE CONFIG DATA ---
    var availableLessons by mutableStateOf<List<Lesson>>(emptyList())
        private set
    var availableTopics by mutableStateOf<List<String>>(emptyList())
        private set

    // --- SEÇİMLER ---
    var selectedLesson by mutableStateOf<Lesson?>(null)
    var selectedTopic by mutableStateOf("")
    var newTaskCount by mutableStateOf("")

    // İşlem Durumu
    var isSaving by mutableStateOf(false)
    var saveSuccess by mutableStateOf(false)
    var isLoadingData by mutableStateOf(false)

    init {
        loadExamConfig()
    }

    // 1. Dersleri Remote Config'den Yükle
    private fun loadExamConfig() {
        scope.launch {
            val config = examRepository.getExamConfig()
            // Şimdilik tüm sınavların derslerini birleştiriyoruz veya belirli bir sınavı seçebilirsin
            // Örn: DGS sınavının derslerini alalım (veya config.exams.first().lessons)
            val allLessons = config.exams.flatMap { it.lessons }.distinctBy { it.name }
            availableLessons = allLessons
        }
    }

    // 2. Tarih Değişince Veriyi Getir
    fun onDateChanged(date: LocalDate, studentId: String) {
        selectedDate = date
        isLoadingData = true
        tasks.clear() // Önce temizle

        scope.launch {
            // Flow'dan sadece ilk veriyi alıp dinlemeyi bırakıyoruz (Single shot)
            val existingSchedule = scheduleRepository.getScheduleForDate(studentId, date.toString()).firstOrNull()

            if (existingSchedule != null) {
                selectedDayType = existingSchedule.type
                tasks.addAll(existingSchedule.tasks)
            } else {
                // Veri yoksa varsayılanlara dön
                selectedDayType = DayType.STUDY
            }
            isLoadingData = false
        }
    }

    // 3. Ders Seçimi
    fun onLessonSelected(lesson: Lesson) {
        selectedLesson = lesson
        availableTopics = lesson.topics
        selectedTopic = "" // Konuyu sıfırla
    }

    // 4. Görev Ekle
    fun addTask() {
        if (selectedLesson == null || selectedTopic.isBlank()) return

        val title = "${selectedLesson!!.name} - $selectedTopic"

        tasks.add(
            ScheduleTask(
                title = title,
                targetCount = newTaskCount.toIntOrNull() ?: 0
            )
        )
        // Seçimleri temizle (İsteğe bağlı, seri ekleme için temizlemeyebilirsin)
        // selectedTopic = ""
        newTaskCount = ""
    }

    // 5. Rutin Ekle
    fun addRoutine() {
        tasks.add(ScheduleTask("Paragraf Rutini", 20))
        tasks.add(ScheduleTask("Problem Rutini", 20))
    }

    fun removeTask(index: Int) {
        tasks.removeAt(index)
    }

    // 6. Kaydet
    fun saveSchedule(coachId: String, studentId: String) {
        isSaving = true
        scope.launch {
            val schedule = ScheduleDay(
                studentId = studentId,
                coachId = coachId,
                date = selectedDate.toString(),
                dayName = selectedDate.dayOfWeek.name,
                type = selectedDayType,
                tasks = tasks.toList()
            )
            scheduleRepository.saveSchedule(schedule)
            isSaving = false
            saveSuccess = true
        }
    }
}