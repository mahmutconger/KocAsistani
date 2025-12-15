package com.anlarsinsoftware.first_kmp_project.ui.student


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anlarsinsoftware.first_kmp_project.ui.components.ActivityHeatmap
import com.anlarsinsoftware.first_kmp_project.viewmodel.StudentDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDashboardScreen(
    userId: String,
    targetExamId: String,
    onAddStudyClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val viewModel = remember { StudentDashboardViewModel() }

    // Ekran açılınca verileri yükle
    LaunchedEffect(Unit) {
        viewModel.loadData(userId, targetExamId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Timofy") }, // Veya Sınav Adı
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Profil")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddStudyClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
               Icon(Icons.Default.Add, contentDescription = "Çalışma Ekle", tint = Color.White)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // 1. HERO SECTION (Geri Sayım)
            HeroSection(examName = viewModel.examName, daysLeft = viewModel.daysLeft)

            Spacer(modifier = Modifier.height(16.dp))

            // 2. HEATMAP (Isı Haritası)
            ActivityHeatmap(logs = viewModel.logs)

            Spacer(modifier = Modifier.height(16.dp))

            // 3. MOTİVASYON VEYA ÖZET
            Text(
                "Bugünkü Durum",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            // Buraya basit bir özet kartı gelebilir
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    "Henüz bugün çalışma girmedin. Hadi başlayalım!",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun HeroSection(examName: String, daysLeft: Long) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primaryContainer
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = examName,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$daysLeft Gün",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Kaldı",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}