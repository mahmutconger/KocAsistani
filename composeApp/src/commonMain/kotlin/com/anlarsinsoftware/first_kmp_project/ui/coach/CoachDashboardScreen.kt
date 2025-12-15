package com.anlarsinsoftware.first_kmp_project.ui.coach


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anlarsinsoftware.first_kmp_project.data.model.User
import com.anlarsinsoftware.first_kmp_project.viewmodel.CoachDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoachDashboardScreen(
    coachId: String,
    onGenerateCodeClick: () -> Unit,
    onStudentClick: (String) -> Unit // Öğrenci detayına gitmek için
) {
    val viewModel = remember { CoachDashboardViewModel() }

    LaunchedEffect(coachId) {
        viewModel.loadStudents(coachId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Öğrencilerim") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.generateCode(coachId) }, // BURAYI DEĞİŞTİRDİK: Direkt ViewModel'e bağladık
                icon = {  },
                text = { Text("Öğrenci Ekle") }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (viewModel.students.isEmpty()) {
                // Hiç öğrenci yoksa boş ekran uyarısı
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                   Icon(Icons.Default.Person, null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Henüz bağlı öğrenciniz yok.")
                    Text("Sağ alttaki butondan kod üretip paylaşın.")
                }
            } else {
                // Öğrenci Listesi
                LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    items(viewModel.students) { student ->
                        StudentCard(student = student, onClick = { onStudentClick(student.id) })
                    }
                }
            }

            if (viewModel.generatedCode != null) {
                InviteCodeDialog(
                    code = viewModel.generatedCode!!,
                    onDismiss = { viewModel.dismissCodeDialog() }
                )
            }
        }
    }
}

@Composable
fun InviteCodeDialog(code: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Öğrenci Davet Kodu",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Bu kodu öğrencinizle paylaşın:")
                Spacer(modifier = Modifier.height(16.dp))

                // Kodu Büyük Göster
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                ) {
                    Text(
                        text = code,
                        style = MaterialTheme.typography.displayMedium, // Kocaman yazı
                        modifier = Modifier.padding(16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 4.sp // Harfleri aç
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Öğrenci bu kodu girdiğinde listenize eklenecektir.", style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tamam")
            }
        }
    )
}

@Composable
fun StudentCard(student: User, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profil İkonu
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(50.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = student.email.take(1).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(text = student.email, style = MaterialTheme.typography.titleMedium) // İsim yoksa email
                Text(
                    text = "Hedef: ${student.targetExam ?: "Belirsiz"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}