package com.anlarsinsoftware.first_kmp_project.ui.student


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anlarsinsoftware.first_kmp_project.data.repository.StudentProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentProfileScreen(
    studentId: String,
    email: String,
    exam: String,
    onBackClick: () -> Unit
) {
    val viewModel = remember { StudentProfileViewModel() }
    var code by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil ve Ayarlar") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Geri")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxWidth()) {

            // 1. KULLANICI BİLGİLERİ KARTI
            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(email, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("Hedef: $exam", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 2. KOÇ BAĞLANTISI ALANI
            Text("Eğitim Koçu Bağlantısı", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Koçunuzun verdiği kodu girerek hesabınızı bağlayın.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = code,
                onValueChange = { code = it.uppercase() }, // Otomatik büyük harf
                label = { Text("Davet Kodu") },
                placeholder = { Text("Örn: X9Y2Z1") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                Button(
                    onClick = { viewModel.linkCoach(studentId, code) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = !viewModel.isSuccess // Başarılıysa butonu kapat
                ) {
                    Text(if (viewModel.isSuccess) "Bağlantı Kuruldu" else "Bağlan")
                }
            }

            // Durum Mesajı
            viewModel.statusMessage?.let { msg ->
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (viewModel.isSuccess) Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(msg, color = if (viewModel.isSuccess) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}