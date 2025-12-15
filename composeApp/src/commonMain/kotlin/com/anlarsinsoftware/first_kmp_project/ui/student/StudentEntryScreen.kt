package com.anlarsinsoftware.first_kmp_project.ui.student


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.anlarsinsoftware.first_kmp_project.viewmodel.StudentEntryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentEntryScreen(
    userId: String,
    targetExamId: String,
    onBackClick: () -> Unit
) {
    val viewModel = remember { StudentEntryViewModel() }

    // Yükleme
    LaunchedEffect(targetExamId) {
        viewModel.loadLessons(targetExamId)
    }

    // Input State'leri
    var solved by remember { mutableStateOf("") }
    var correct by remember { mutableStateOf("") }
    var wrong by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Çalışma Ekle") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            // --- 1. DERS SEÇİMİ ---
            SimpleDropdown(
                label = "Ders Seç",
                options = viewModel.availableLessons.map { it.name },
                selectedOption = viewModel.selectedLesson?.name ?: "",
                onOptionSelected = { name ->
                    val lesson = viewModel.availableLessons.find { it.name == name }
                    if (lesson != null) viewModel.onLessonSelected(lesson)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- 2. KONU SEÇİMİ ---
            SimpleDropdown(
                label = "Konu Seç",
                options = viewModel.availableTopics,
                selectedOption = viewModel.selectedTopic,
                onOptionSelected = { viewModel.onTopicSelected(it) },
                enabled = viewModel.selectedLesson != null
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- 3. SAYISAL GİRİŞLER ---
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = solved,
                    onValueChange = { solved = it },
                    label = { Text("Soru") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = correct,
                    onValueChange = { correct = it },
                    label = { Text("Doğru") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = wrong,
                    onValueChange = { wrong = it },
                    label = { Text("Yanlış") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- KAYDET BUTONU ---
            if (viewModel.isSaving) {
                CircularProgressIndicator(modifier = Modifier.fillMaxWidth().wrapContentWidth())
            } else {
                Button(
                    onClick = {
                        viewModel.saveStudyLog(userId, solved, correct, wrong) {
                            onBackClick() // Başarılı olunca geri dön
                        }
                    },
                    enabled = viewModel.selectedLesson != null && viewModel.selectedTopic.isNotBlank() && solved.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("KAYDET")
                }
            }
        }
    }
}

// Yardımcı Basit Dropdown Bileşeni
@Composable
fun SimpleDropdown(
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
            readOnly = true, // Klavye açılmasın
            enabled = enabled,
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
            modifier = Modifier.fillMaxWidth().clickable { if (enabled) expanded = true }
        )

        // Şeffaf buton: Tıklamayı yakalamak için TextField'ın üzerine koyuyoruz
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(enabled = enabled) { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f) // Genişlik ayarı
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