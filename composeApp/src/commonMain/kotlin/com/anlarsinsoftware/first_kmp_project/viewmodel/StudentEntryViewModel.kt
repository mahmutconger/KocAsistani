package com.anlarsinsoftware.first_kmp_project.viewmodel


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.anlarsinsoftware.first_kmp_project.data.model.Lesson
import com.anlarsinsoftware.first_kmp_project.data.model.StudyLog
import com.anlarsinsoftware.first_kmp_project.data.repository.ExamRepository
import com.anlarsinsoftware.first_kmp_project.data.repository.StudyRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

class StudentEntryViewModel {
    private val examRepository = ExamRepository()
    private val studyRepository = StudyRepository()
    private val scope = CoroutineScope(Dispatchers.IO)

    // Ekranda gösterilecek listeler
    var availableLessons by mutableStateOf<List<Lesson>>(emptyList())
        private set

    var availableTopics by mutableStateOf<List<String>>(emptyList())
        private set

    // Seçilen değerler
    var selectedLesson by mutableStateOf<Lesson?>(null)
        private set

    var selectedTopic by mutableStateOf("")
        private set

    var isSaving by mutableStateOf(false)
        private set

    // 1. Ekran açılınca dersleri yükle
    fun loadLessons(targetExamId: String) {
        scope.launch {
            val config = examRepository.getExamConfig()
            val exam = config.exams.find { it.id == targetExamId }
            availableLessons = exam?.lessons ?: emptyList()
        }
    }

    // 2. Ders seçilince konuları güncelle
    fun onLessonSelected(lesson: Lesson) {
        selectedLesson = lesson
        availableTopics = lesson.topics
        selectedTopic = "" // Konu seçimini sıfırla
    }

    fun onTopicSelected(topic: String) {
        selectedTopic = topic
    }

    // 3. Kaydet
    fun saveStudyLog(
        userId: String,
        solved: String,
        correct: String,
        wrong: String,
        onSuccess: () -> Unit
    ) {
        if (selectedLesson == null || selectedTopic.isBlank()) return

        isSaving = true
        scope.launch {
            try {
                val log = StudyLog(
                    userId = userId,
                    lesson = selectedLesson!!.name,
                    topic = selectedTopic,
                    count = solved.toIntOrNull() ?: 0,
                    correct = correct.toIntOrNull() ?: 0,
                    wrong = wrong.toIntOrNull() ?: 0
                )

                studyRepository.addStudyLog(log)
                onSuccess() // Başarılı olunca ekranı kapatacağız
            } catch (e: Exception) {
                // Hata yönetimi eklenebilir
                println("Kayıt hatası: ${e.message}")
            } finally {
                isSaving = false
            }
        }
    }
}