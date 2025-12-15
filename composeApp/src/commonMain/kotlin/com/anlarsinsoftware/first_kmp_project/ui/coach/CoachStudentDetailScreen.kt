package com.anlarsinsoftware.first_kmp_project.ui.coach

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anlarsinsoftware.first_kmp_project.ui.components.ActivityHeatmap
import com.anlarsinsoftware.first_kmp_project.ui.components.StudyLogItem
import com.anlarsinsoftware.first_kmp_project.viewmodel.StudentDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoachStudentDetailScreen(
    studentId: String,
    onBackClick: () -> Unit
) {
    val viewModel = remember { StudentDetailViewModel() }

    LaunchedEffect(studentId) {
        viewModel.loadStudentLogs(studentId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Öğrenci Analizi") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Geri")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {

            // 1. ISI HARİTASI (Öğrencinin aktivitesi)
            Text("Son 4 Hafta", style = MaterialTheme.typography.titleMedium)
            ActivityHeatmap(logs = viewModel.logs)

            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))

            // 2. SON AKTİVİTELER LİSTESİ
            Text("Son Çalışmalar", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (viewModel.logs.isEmpty()) {
                Text("Henüz veri girişi yok.", color = MaterialTheme.colorScheme.secondary)
            } else {
                LazyColumn {
                    items(viewModel.logs) { log ->
                        StudyLogItem(log = log)
                    }
                }
            }
        }
    }
}