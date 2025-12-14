package com.anlarsinsoftware.first_kmp_project

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anlarsinsoftware.first_kmp_project.data.model.StudySession
import com.anlarsinsoftware.first_kmp_project.data.service.FirebaseService
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch

@Composable
@Preview
fun App() {
    MaterialTheme {
        val scope = rememberCoroutineScope()

        // Firebase servisinin örneğini al
        val firebaseService = remember { FirebaseService() }

        // İlk kayıt işlemini yapmak için bir Composable
        Column(Modifier.fillMaxSize().padding(20.dp), Arrangement.Center) {

            Text("Koç Asistanı V0.1 Hazır")

            Spacer(Modifier.height(20.dp))

            Button(onClick = {
                // Coroutine başlattık çünkü Firestore işlemleri askıya alma (suspend) gerektirir.
                scope.launch {
                    val testSession = StudySession(
                        studentId = "TEST_OGRENCI_1", // Test öğrencisi ID'si
                        subjectName = "Matematik",
                        topicName = "Türev",
                        solvedCount = 50,
                        correctCount = 40,
                        wrongCount = 10
                    )

                    try {
                        firebaseService.saveStudySession(testSession)
                        println("Başarılı! Veri Firestore'a kaydedildi: ${testSession.topicName}")
                    } catch (e: Exception) {
                        println("HATA: Kayıt yapılırken sorun oluştu: ${e.message}")
                    }
                }
            }) {
                Text("Firestore'a Test Verisi Kaydet")
            }
        }
    }
}