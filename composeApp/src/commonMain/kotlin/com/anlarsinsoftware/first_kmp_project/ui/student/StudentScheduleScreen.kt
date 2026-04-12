package com.anlarsinsoftware.first_kmp_project.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anlarsinsoftware.first_kmp_project.data.model.DayType
import com.anlarsinsoftware.first_kmp_project.ui.components.WeekCalendarView
import com.anlarsinsoftware.first_kmp_project.viewmodel.StudentScheduleViewModel
import kotlinx.datetime.LocalDate

// --- AYNI RENK PALETİ (Tutarlılık için) ---
val BlueGradientStart = Color(0xFF4FC3F7)
val BlueGradientEnd = Color(0xFF0288D1)
val ScreenBackground = Color(0xFFF8F9FA)
val CardBackground = Color.White
val SuccessGreen = Color(0xFF4CAF50)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentScheduleScreen(
    studentId: String,
    onBackClick: () -> Unit // Geri butonu için parametre ekleyelim (App.kt'den gelebilir) veya boş bırakabilirsin
) {
    val viewModel = remember { StudentScheduleViewModel() }

    // İlk açılışta veriyi yükle
    LaunchedEffect(Unit) {
        viewModel.onDateSelected(viewModel.selectedDate, studentId)
    }

    Scaffold(
        containerColor = ScreenBackground,
        topBar = {
            StudentScheduleHeader(
                title = "Ders Programım",
                onBackClick = onBackClick // Eğer App.kt'de tanımlamadıysan {} verebilirsin
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 1. TAKVİM KARTI
            SectionCard(title = "Takvim") {
                WeekCalendarView(
                    selectedDate = viewModel.selectedDate,
                    onDateSelected = { newDate ->
                        viewModel.onDateSelected(newDate, studentId)
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. İÇERİK ALANI (Dinamik)
            val schedule = viewModel.scheduleDay

            if (schedule == null) {
                // Program Yoksa
                EmptyStateCard(
                    emoji = "🎉",
                    title = "Program Bulunamadı",
                    subtitle = "Bugün için atanmış bir görev yok.\nKendine vakit ayır!"
                )
            } else {
                when (schedule.type) {
                    DayType.REST -> {
                        EmptyStateCard(
                            emoji = "☕",
                            title = "Bugün Tatil!",
                            subtitle = "Dinlen ve enerjini topla."
                        )
                    }

                    else -> { // STUDY veya FULL_REPEAT

                        // --- GÖREV LİSTESİ ---
                        Text(
                            "Yapılacaklar (${schedule.tasks.count { it.isDone }}/${schedule.tasks.size})",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyColumn(modifier = Modifier.weight(1f)) {
                            itemsIndexed(schedule.tasks) { index, task ->
                                StudentTaskCard(
                                    title = task.title,
                                    targetCount = task.targetCount,
                                    isDone = task.isDone,
                                    onToggle = { isChecked ->
                                        viewModel.toggleTask(studentId, index, isChecked)
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // --- KOÇ DEĞERLENDİRMESİ VE NOT ALANI ---
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                            // Koç Yorumu Varsa
                            if (schedule.coachRating > 0 || schedule.coachComment.isNotBlank()) {
                                CoachFeedbackCard(
                                    rating = schedule.coachRating,
                                    comment = schedule.coachComment
                                )
                            }

                            // Öğrenci Notu Girişi
                            StudentNoteInput(
                                note = viewModel.userNote,
                                onNoteChange = { viewModel.onNoteChange(it) },
                                onSave = { viewModel.saveNote(studentId) }
                            )
                        }

                        // Klavye açılınca altta boşluk kalsın
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

// --- YARDIMCI BİLEŞENLER ---

@Composable
fun StudentScheduleHeader(title: String, onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(
                Brush.horizontalGradient(listOf(BlueGradientStart, BlueGradientEnd))
            )
            .statusBarsPadding() // Status bar altına indir
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Geri butonu opsiyonel, eğer navigation stack varsa gösterilir
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    .size(40.dp)
            ) {
                Icon(Icons.Default.ArrowBack, "Geri", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SectionCard(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = Color.Gray,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            elevation = CardDefaults.cardElevation(2.dp),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
fun StudentTaskCard(
    title: String,
    targetCount: Int,
    isDone: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDone) Color(0xFFF1F8E9) else Color.White // Tamamlanınca çok açık yeşil
        ),
        elevation = CardDefaults.cardElevation(if (isDone) 0.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isDone,
                onCheckedChange = onToggle,
                colors = CheckboxDefaults.colors(
                    checkedColor = SuccessGreen,
                    checkmarkColor = Color.White
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isDone) FontWeight.Normal else FontWeight.SemiBold,
                    textDecoration = if (isDone) TextDecoration.LineThrough else null,
                    color = if (isDone) Color.Gray else Color.Black
                )
                if (targetCount > 0) {
                    Text(
                        text = "Hedef: $targetCount Soru",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isDone) Color.LightGray else BlueGradientEnd
                    )
                }
            }

            if (isDone) {
                Icon(Icons.Default.CheckCircle, null, tint = SuccessGreen, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun EmptyStateCard(emoji: String, title: String, subtitle: String) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 64.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = Color.LightGray, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

@Composable
fun CoachFeedbackCard(rating: Int, comment: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)), // Açık Sarı Arka Plan
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Koç Değerlendirmesi", fontWeight = FontWeight.Bold, color = Color(0xFFFFA000))
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Yıldızlar
            Row {
                repeat(5) { index ->
                    Icon(
                        imageVector = if (index < rating) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (comment.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(comment, style = MaterialTheme.typography.bodyMedium, color = Color.Black.copy(0.7f))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentNoteInput(
    note: String,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        TextField(
            value = note,
            onValueChange = onNoteChange,
            placeholder = { Text("Günü değerlendir (Not ekle)...", fontSize = 14.sp) },
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            leadingIcon = { Icon(Icons.Default.Edit, null, tint = Color.LightGray) }
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Kaydet Butonu (Küçük yuvarlak veya ikon buton)
        IconButton(
            onClick = onSave,
            modifier = Modifier
                .background(BlueGradientEnd, CircleShape)
                .size(50.dp)
        ) {
            Icon(Icons.Default.CheckCircle, "Kaydet", tint = Color.White)
        }
    }
}