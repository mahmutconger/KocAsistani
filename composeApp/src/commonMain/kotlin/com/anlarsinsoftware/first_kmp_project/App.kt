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
import com.anlarsinsoftware.first_kmp_project.ui.coach.CoachDashboardScreen
import com.anlarsinsoftware.first_kmp_project.ui.coach.CoachStudentDetailScreen
import com.anlarsinsoftware.first_kmp_project.ui.student.StudentDashboardScreen
import com.anlarsinsoftware.first_kmp_project.ui.student.StudentEntryScreen
import com.anlarsinsoftware.first_kmp_project.ui.student.StudentProfileScreen
import com.anlarsinsoftware.first_kmp_project.ui.student.StudentSetupScreen
import com.anlarsinsoftware.first_kmp_project.viewmodel.AuthState
import com.anlarsinsoftware.first_kmp_project.viewmodel.AuthViewModel
enum class StudentNav { DASHBOARD, ENTRY, PROFILE }
enum class CoachNav { DASHBOARD, DETAIL }

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
                                        onProfileClick = { studentNav = StudentNav.PROFILE } // YENİ
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
                                        onBackClick = { studentNav = StudentNav.DASHBOARD }
                                    )
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
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}