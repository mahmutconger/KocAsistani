package com.anlarsinsoftware.first_kmp_project.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anlarsinsoftware.first_kmp_project.viewmodel.StudentEntryViewModel

// Renkler (Diğer ekranlarla aynı)


@Composable
fun StudentEntryScreen(
    userId: String,
    targetExamId: String,
    onBackClick: () -> Unit
) {
    val BlueGradientStart = Color(0xFF4FC3F7)
    val BlueGradientEnd = Color(0xFF0288D1)
    val ScreenBackground = Color(0xFFF8F9FA)
    val CardBackground = Color.White
    // ViewModel içinde loadLessons fonksiyonu ExamRepository'i kullanmalı
    // ViewModel kodunu güncellemediysen, repository.getExamConfig() çağırdığından emin ol.
    val viewModel = remember { StudentEntryViewModel() }

    LaunchedEffect(targetExamId) {
        viewModel.loadLessons(targetExamId)
    }

    // Input State'leri
    var solved by remember { mutableStateOf("") }
    var correct by remember { mutableStateOf("") }
    var wrong by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = ScreenBackground,
        topBar = {
            EntryHeader(title = "Çalışma Ekle", onBackClick = onBackClick)
        },
        bottomBar = {
            // Alt Kısımda Büyük Kaydet Butonu
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.saveStudyLog(userId, solved, correct, wrong) {
                            onBackClick()
                        }
                    },
                    enabled = !viewModel.isSaving && viewModel.selectedLesson != null && viewModel.selectedTopic.isNotBlank() && solved.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BlueGradientEnd,
                        disabledContainerColor = Color.Gray
                    )
                ) {
                    if (viewModel.isSaving) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("KAYDET", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState) // Scroll eklendi
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // --- 1. DERS VE KONU SEÇİM KARTI ---
            KocSectionTitle("Ne Çalıştın?")
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Ders Seçimi
                    StylishDropdown(
                        label = "Ders Seç",
                        options = viewModel.availableLessons.map { it.name },
                        selectedOption = viewModel.selectedLesson?.name ?: "",
                        onOptionSelected = { name ->
                            val lesson = viewModel.availableLessons.find { it.name == name }
                            if (lesson != null) viewModel.onLessonSelected(lesson)
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Konu Seçimi
                    StylishDropdown(
                        label = "Konu Seç",
                        options = viewModel.availableTopics,
                        selectedOption = viewModel.selectedTopic,
                        onOptionSelected = { viewModel.onTopicSelected(it) },
                        enabled = viewModel.selectedLesson != null
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- 2. SAYISAL VERİLER KARTI ---
            KocSectionTitle("Sonuçlar")
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StylishNumberInput(
                        value = solved,
                        onValueChange = { solved = it },
                        label = "Soru",
                        modifier = Modifier.weight(1f)
                    )
                    StylishNumberInput(
                        value = correct,
                        onValueChange = { correct = it },
                        label = "Doğru",
                        modifier = Modifier.weight(1f),
                        textColor = Color(0xFF2E7D32) // Yeşil
                    )
                    StylishNumberInput(
                        value = wrong,
                        onValueChange = { wrong = it },
                        label = "Yanlış",
                        modifier = Modifier.weight(1f),
                        textColor = Color(0xFFC62828) // Kırmızı
                    )
                }
            }

            // BottomBar altında kalmaması için boşluk
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

// --- YARDIMCI BİLEŞENLER ---

@Composable
fun EntryHeader(title: String, onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(Brush.horizontalGradient(listOf(BlueGradientStart, BlueGradientEnd)))
            .statusBarsPadding()
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
fun KocSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = Color.Gray,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
    )
}

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
                focusedLabelColor = BlueGradientEnd
            )
        )

        // TextField üzerine görünmez tıklama alanı
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(enabled = enabled) { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .background(Color.White)
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

@Composable
fun StylishNumberInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Black
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BlueGradientEnd,
            unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
            focusedLabelColor = BlueGradientEnd,
            focusedTextColor = textColor,
            unfocusedTextColor = textColor
        ),
        singleLine = true
    )
}