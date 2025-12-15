package com.anlarsinsoftware.first_kmp_project.ui.student

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anlarsinsoftware.first_kmp_project.data.model.Exam
import com.anlarsinsoftware.first_kmp_project.data.repository.ExamRepository
import kotlinx.coroutines.launch

@Composable
fun StudentSetupScreen(
    onExamSelected: (Exam) -> Unit
) {
    val repository = remember { ExamRepository() }
    val scope = rememberCoroutineScope()
    var exams by remember { mutableStateOf<List<Exam>>(emptyList()) }

    // Ekran açılınca sınav listesini yükle
    LaunchedEffect(Unit) {
        exams = repository.getExamConfig().exams
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            "Hedefin Ne?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            "Hazırlandığın sınavı seç, sana özel programı hazırlayalım.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (exams.isEmpty()) {
            CircularProgressIndicator()
        } else {
            LazyColumn {
                items(exams) { exam ->
                    ExamSelectionCard(exam = exam, onClick = { onExamSelected(exam) })
                }
            }
        }
    }
}

@Composable
fun ExamSelectionCard(exam: Exam, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = exam.id,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = exam.name,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Sağ tarafa küçük bir ok ikonu koyabiliriz ama şimdilik gerek yok
        }
    }
}