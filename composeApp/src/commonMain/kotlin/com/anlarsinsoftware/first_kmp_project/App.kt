package com.anlarsinsoftware.first_kmp_project

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.anlarsinsoftware.first_kmp_project.data.model.Role
import com.anlarsinsoftware.first_kmp_project.ui.auth.LoginScreen
import com.anlarsinsoftware.first_kmp_project.ui.auth.RoleSelectionScreen
import com.anlarsinsoftware.first_kmp_project.ui.chat.ChatScreen
import com.anlarsinsoftware.first_kmp_project.ui.coach.CoachDashboardScreen
import com.anlarsinsoftware.first_kmp_project.ui.coach.CoachStudentDetailScreen
import com.anlarsinsoftware.first_kmp_project.ui.coach.schedule.CoachScheduleScreen
import com.anlarsinsoftware.first_kmp_project.ui.student.FocusDashboardScreen
import com.anlarsinsoftware.first_kmp_project.ui.student.FocusTimerScreen
import com.anlarsinsoftware.first_kmp_project.ui.student.StudentDashboardScreen
import com.anlarsinsoftware.first_kmp_project.ui.student.StudentEntryScreen
import com.anlarsinsoftware.first_kmp_project.ui.student.StudentScheduleScreen
import com.anlarsinsoftware.first_kmp_project.ui.student.StudentSetupScreen
import com.anlarsinsoftware.first_kmp_project.ui.student.profile.StudentProfileScreen
import com.anlarsinsoftware.first_kmp_project.viewmodel.AuthState
import com.anlarsinsoftware.first_kmp_project.viewmodel.AuthViewModel

enum class StudentNav { DASHBOARD, ENTRY, PROFILE, CHAT , SCHEDULE, FOCUS_DASHBOARD, FOCUS_TIMER}
enum class CoachNav { DASHBOARD, DETAIL, CHAT, SCHEDULE }

@Composable
fun App() {
    MaterialTheme {
        val authViewModel = remember { AuthViewModel() }

        when (authViewModel.authState) {
            AuthState.IDLE, AuthState.LOGIN -> {
                LoginScreen(
                    onAuthAction = { email, pass, isRegister ->
                        authViewModel.onLoginOrRegister(email, pass, isRegister)
                    },
                    isLoading = authViewModel.isLoading,
                    error = authViewModel.errorMessage
                )
            }

            AuthState.ROLE_SELECTION -> {
                RoleSelectionScreen(
                    onRoleSelected = { role -> authViewModel.onRoleSelected(role) },
                    isLoading = authViewModel.isLoading
                )
            }

            AuthState.COMPLETED -> {
                val user = authViewModel.currentUser

                // Kullanıcı null ise (olmamalı ama) Login'e dön
                if (user == null) {
                    // Hata durumu, login'e atılabilir
                } else {
                    // --- ÖĞRENCİ AKIŞI ---
                    if (user.role == Role.STUDENT.name) {

                        // Sınav seçmiş mi?
                        if (user.targetExam.isNullOrBlank()) {
                            // SEÇMEMİŞ -> KURULUM EKRANI
                            StudentSetupScreen(
                                onExamSelected = { exam ->
                                    authViewModel.onExamSelected(exam.id)
                                }
                            )
                        } else {
                            // --- ÖĞRENCİ İÇİ NAVİGASYON ---
                            var studentNav by remember { mutableStateOf(StudentNav.DASHBOARD) }

                            when (studentNav) {
                                StudentNav.DASHBOARD -> {
                                    StudentDashboardScreen(
                                        userId = user.id,
                                        targetExamId = user.targetExam,
                                        onAddStudyClick = { studentNav = StudentNav.ENTRY },
                                        onProfileClick = { studentNav = StudentNav.PROFILE },
                                        onChatClick = { studentNav = StudentNav.CHAT },
                                        onScheduleClick = {studentNav = StudentNav.SCHEDULE},
                                        onFocusClick = { studentNav = StudentNav.FOCUS_DASHBOARD }
                                    )
                                }

                                StudentNav.ENTRY -> {
                                    StudentEntryScreen(
                                        userId = user.id,
                                        targetExamId = user.targetExam,
                                        onBackClick = {
                                            // Geri basınca Dashboard'a dön
                                            studentNav = StudentNav.DASHBOARD
                                        }
                                    )
                                }

                                StudentNav.PROFILE -> {
                                    StudentProfileScreen(
                                        studentId = user.id,
                                        email = user.email,
                                        exam = user.targetExam,
                                        onBackClick = { studentNav = StudentNav.DASHBOARD },
                                        onSignOut = {
                                            authViewModel.signOut()
                                        }
                                    )
                                }
                                StudentNav.FOCUS_DASHBOARD -> {
                                    FocusDashboardScreen(
                                        onBackClick = { studentNav = StudentNav.DASHBOARD },
                                        onStartFocusClick = { studentNav = StudentNav.FOCUS_TIMER }
                                    )
                                }

                                // YENİ: SAYAÇ EKRANI
                                StudentNav.FOCUS_TIMER -> {
                                    FocusTimerScreen(
                                        studentId = user.id,
                                        onCloseClick = {
                                            // Timer kapatılınca Dashboard'a veya Focus özetine dön
                                            studentNav = StudentNav.FOCUS_DASHBOARD
                                        }
                                    )
                                }

                                StudentNav.CHAT -> {
                                    // Eğer öğrencinin koçu yoksa burada uyarı verebiliriz ama şimdilik bağlı varsayalım
                                    if (user.coachId != null) {
                                        ChatScreen(
                                            currentUserId = user.id,
                                            coachId = user.coachId, // Öğrencinin koçu
                                            studentId = user.id,    // Kendisi
                                            chatTitle = "Koçum",
                                            onBackClick = { studentNav = StudentNav.DASHBOARD }
                                        )
                                    } else {
                                        // Koç yoksa Profile yönlendir
                                        studentNav = StudentNav.PROFILE
                                    }
                                }

                                StudentNav.SCHEDULE -> {
                                    StudentScheduleScreen(studentId = user.id, onBackClick = { studentNav = StudentNav.DASHBOARD})
                                    // Geri butonu eklemek istersen Scaffold'u buraya da taşıyabiliriz,
                                    // ya da Android'in geri tuşuna güvenebiliriz.
                                    // Şimdilik en alta bir "Geri Dön" butonu koyabilirsin test için.
                                }
                            }
                        }
                    }
                    // --- KOÇ AKIŞI ---
                    else if (user.role == Role.COACH.name) {
                        // Koç Navigasyon State'i
                        var coachNav by remember { mutableStateOf(CoachNav.DASHBOARD) }
                        var selectedStudentId by remember { mutableStateOf("") }

                        when (coachNav) {
                            CoachNav.DASHBOARD -> {
                                CoachDashboardScreen(
                                    coachId = user.id,
                                    onGenerateCodeClick = { /* Kod üretme buradaydı */ },
                                    onStudentClick = { studentId ->
                                        // Öğrenciye tıklanınca ID'yi al ve detay ekranına geç
                                        selectedStudentId = studentId
                                        coachNav = CoachNav.DETAIL
                                    }
                                )
                            }

                            CoachNav.DETAIL -> {
                                CoachStudentDetailScreen(
                                    studentId = selectedStudentId,
                                    onBackClick = {
                                        coachNav = CoachNav.DASHBOARD
                                    },
                                    onChatClick ={
                                        coachNav = CoachNav.CHAT
                                    },
                                    onScheduleClick = { coachNav = CoachNav.SCHEDULE },
                                    coachId = user.coachId.toString()
                                )
                            }
                            CoachNav.CHAT -> {
                                ChatScreen(
                                    currentUserId = user.id,
                                    coachId = user.id,          // Kendisi
                                    studentId = selectedStudentId, // Seçili öğrenci
                                    chatTitle = "Öğrenci Sohbeti",
                                    onBackClick = { coachNav = CoachNav.DETAIL }
                                )
                            }
                            CoachNav.SCHEDULE -> {
                                CoachScheduleScreen(
                                    coachId = user.id,
                                    studentId = selectedStudentId,
                                    onBackClick = { coachNav = CoachNav.DETAIL }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}