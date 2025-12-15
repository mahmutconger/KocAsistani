package com.anlarsinsoftware.first_kmp_project.data.repository

import com.anlarsinsoftware.first_kmp_project.data.model.AppConfig
import com.anlarsinsoftware.first_kmp_project.data.model.Exam
import com.anlarsinsoftware.first_kmp_project.data.model.Lesson

class ExamRepository {

    // İLERDE BURASI REMOTE CONFIG OLACAK
    // Şimdilik hızlı ilerlemek için manuel listeyi buraya gömüyoruz.
    suspend fun getExamConfig(): AppConfig {
        return AppConfig(
            exams = listOf(
                Exam(
                    id = "YKS",
                    name = "YKS (TYT-AYT)",
                    date = "2026-06-20",
                    lessons = listOf(
                        Lesson("tyt_mat", "TYT Matematik", listOf("Temel Kavramlar", "Problemler")),
                        Lesson("ayt_mat", "AYT Matematik", listOf("Türev", "İntegral")),
                        Lesson("tyt_turkce", "TYT Türkçe", listOf("Paragraf", "Dil Bilgisi"))
                    )
                ),
                Exam(
                    id = "DGS",
                    name = "Dikey Geçiş Sınavı",
                    date = "2026-06-30",
                    lessons = listOf(
                        Lesson("dgs_sayisal", "Sayısal (Mat-Geo)", listOf("Sayılar", "Problemler", "Sayısal Mantık")),
                        Lesson("dgs_sozel", "Sözel (Türkçe)", listOf("Sözcükte Anlam", "Sözel Mantık"))
                    )
                ),
                Exam(
                    id = "LGS",
                    name = "Liselere Geçiş Sistemi",
                    date = "2026-06-06",
                    lessons = listOf(
                        Lesson("lgs_mat", "Matematik", listOf("Çarpanlar", "Üslü Sayılar")),
                        Lesson("lgs_fen", "Fen Bilimleri", listOf("Mevsimler", "DNA"))
                    )
                ),
                Exam(
                    id = "KPSS",
                    name = "KPSS Lisans",
                    date = "2026-07-14",
                    lessons = listOf(
                        Lesson("kpss_gy", "Genel Yetenek", listOf("Matematik", "Türkçe")),
                        Lesson("kpss_gk", "Genel Kültür", listOf("Tarih", "Coğrafya"))
                    )
                )
            )
        )
    }
}