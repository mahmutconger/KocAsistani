package com.anlarsinsoftware.first_kmp_project.ui.coach.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anlarsinsoftware.first_kmp_project.data.model.DayType
import com.anlarsinsoftware.first_kmp_project.ui.components.WeekCalendarView
import com.anlarsinsoftware.first_kmp_project.viewmodel.CoachScheduleViewModel

// --- RENK PALETİ ---
val BlueGradientStart = Color(0xFF4FC3F7)
val BlueGradientEnd = Color(0xFF0288D1)
val ScreenBackground = Color(0xFFF8F9FA)
val CardBackground = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoachScheduleScreen(
    coachId: String,
    studentId: String,
    onBackClick: () -> Unit
) {
    val viewModel = remember { CoachScheduleViewModel() }

    // Ekran açılınca veya tarih değişince veriyi yükle
    // Not: İlk açılışta viewModel.selectedDate init bloğunda set ediliyor.
    // Ancak öğrenci verisini çekmek için LaunchedEffect kullanıyoruz.
    LaunchedEffect(viewModel.selectedDate) {
        viewModel.onDateChanged(viewModel.selectedDate, studentId)
    }

    LaunchedEffect(viewModel.saveSuccess) {
        if (viewModel.saveSuccess) {
            onBackClick()
        }
    }

    Scaffold(
        containerColor = ScreenBackground,
        topBar = {
            ScheduleHeader(
                title = "Program Oluştur",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            // DÜZELTİLEN KISIM: Surface kullanımı ve padding
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 16.dp, // Belirgin gölge
                color = Color.White,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .navigationBarsPadding() // Alt barın altında kalmaması için
                ) {
                    GradientButton(
                        text = if (viewModel.isSaving) "KAYDEDİLİYOR..." else "PROGRAMI KAYDET",
                        onClick = { viewModel.saveSchedule(coachId, studentId) },
                        enabled = !viewModel.isSaving
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            // BottomBar butonunun yüksekliği kadar alttan boşluk bırakıyoruz
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {

            // 1. BOŞLUK (item içinde olmalı)
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // 2. TARİH SEÇİMİ
            item {
                SectionCard(title = "Tarih Seçimi") {
                    WeekCalendarView(
                        selectedDate = viewModel.selectedDate,
                        onDateSelected = { viewModel.onDateChanged(it, studentId) } // Düzeltildi
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Loading kontrolü (Opsiyonel)
            if (viewModel.isLoadingData) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = BlueGradientEnd)
                    }
                }
            } else {
                // 3. GÜN TÜRÜ SEÇİMİ
                item {
                    SectionCard(title = "Günün Modu") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            DayType.values().forEach { type ->
                                CustomChip(
                                    text = when(type) {
                                        DayType.STUDY -> "📚 Ders"
                                        DayType.REST -> "☕ Tatil"
                                        DayType.FULL_REPEAT -> "🔄 Tekrar"
                                    },
                                    isSelected = viewModel.selectedDayType == type,
                                    onSelect = { viewModel.selectedDayType = type }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 4. GÖREV EKLEME ALANI
                if (viewModel.selectedDayType != DayType.REST) {

                    item {
                        // Rutin Ekle Butonu
                        OutlinedButton(
                            onClick = { viewModel.addRoutine() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = BlueGradientEnd)
                        ) {
                            Icon(Icons.Default.Add, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Otomatik Rutin Ekle")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        // Yeni Görev Ekleme Kartı
                        SectionCard(title = "Yeni Görev Ekle") {
                            Column {
                                StylishDropdown(
                                    label = "Ders Seç",
                                    options = viewModel.availableLessons.map { it.name },
                                    selectedOption = viewModel.selectedLesson?.name ?: "",
                                    onOptionSelected = { name ->
                                        val lesson = viewModel.availableLessons.find { it.name == name }
                                        if (lesson != null) viewModel.onLessonSelected(lesson)
                                    }
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                StylishDropdown(
                                    label = "Konu Seç",
                                    options = viewModel.availableTopics,
                                    selectedOption = viewModel.selectedTopic,
                                    onOptionSelected = { viewModel.selectedTopic = it },
                                    enabled = viewModel.selectedLesson != null
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    StylishTextField(
                                        value = viewModel.newTaskCount,
                                        onValueChange = { viewModel.newTaskCount = it },
                                        placeholder = "Hedef Soru",
                                        keyboardType = KeyboardType.Number,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    FloatingActionButton(
                                        onClick = { viewModel.addTask() },
                                        containerColor = BlueGradientEnd,
                                        contentColor = Color.White,
                                        modifier = Modifier.size(56.dp),
                                        shape = CircleShape
                                    ) {
                                        Icon(Icons.Default.Add, "Ekle")
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            "Görev Listesi (${viewModel.tasks.size})",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // --- EKLENEN GÖREVLER LİSTESİ (Burada item {} kullanmıyoruz) ---
                    // LazyColumn'ın kendi items fonksiyonunu kullanıyoruz
                    itemsIndexed(viewModel.tasks) { index, task ->
                        TaskItemCard(
                            title = task.title,
                            count = task.targetCount,
                            onDelete = { viewModel.removeTask(index) }
                        )
                    }

                } else {
                    // Tatil Modu Görünümü
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp), // Yükseklik verelim ki ortalansın
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("☕", fontSize = 64.sp)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Öğrenciye tatil verdiniz.", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- YARDIMCI BİLEŞENLER (Custom Components) ---

@Composable
fun StylishDropdown(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            enabled = enabled,
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = enabled) { expanded = true },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BlueGradientEnd,
                unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                focusedLabelColor = BlueGradientEnd,
                disabledBorderColor = Color.LightGray.copy(alpha = 0.2f)
            )
        )

        Box(modifier = Modifier.matchParentSize().clickable(enabled = enabled) { expanded = true })

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.85f).background(Color.White)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

// (Diğer bileşenler: ScheduleHeader, SectionCard, CustomChip, TaskItemCard, GradientButton, StylishTextField
// önceki kodundakiyle aynı kalabilir. StylishTextField'i aşağıya ekliyorum tam olması için)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StylishTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(placeholder) },
        modifier = modifier.height(60.dp), // Biraz daha yüksek
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BlueGradientEnd,
            unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
            focusedLabelColor = BlueGradientEnd
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true
    )
}

@Composable
fun ScheduleHeader(title: String, onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp) // Biraz yüksek header
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(BlueGradientStart, BlueGradientEnd)
                )
            )
            .statusBarsPadding() // Status bar'ın altına inmesi için
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
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
fun CustomChip(text: String, isSelected: Boolean, onSelect: () -> Unit) {
    val backgroundColor = if (isSelected) BlueGradientEnd else Color.White
    val contentColor = if (isSelected) Color.White else Color.Gray
    val borderColor = if (isSelected) Color.Transparent else Color.LightGray

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .clickable { onSelect() }
            .then(
                if (!isSelected) Modifier.border(1.dp, borderColor, RoundedCornerShape(50))
                else Modifier
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = contentColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun TaskItemCard(title: String, count: Int, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(BlueGradientStart.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.List, null, tint = BlueGradientEnd)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(title, fontWeight = FontWeight.Bold, color = Color.Black)
                    if (count > 0) {
                        Text("$count Soru Hedef", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "Sil", tint = Color(0xFFFF5252))
            }
        }
    }
}

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent, // Gradient için şeffaf yapıyoruz
            disabledContainerColor = Color.Gray
        ),
        contentPadding = PaddingValues(), // Padding'i sıfırla ki Box dolsun
        shape = RoundedCornerShape(14.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (enabled) Brush.horizontalGradient(listOf(BlueGradientStart, BlueGradientEnd))
                    else Brush.linearGradient(listOf(Color.Gray, Color.Gray))
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}