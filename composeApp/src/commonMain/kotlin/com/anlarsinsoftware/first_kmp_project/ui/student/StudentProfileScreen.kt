package com.anlarsinsoftware.first_kmp_project.ui.student.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anlarsinsoftware.first_kmp_project.viewmodel.StudentProfileViewModel

// --- RENK PALETİ (Diğer ekranlarla uyumlu) ---
val BlueGradientStart = Color(0xFF4FC3F7)
val BlueGradientEnd = Color(0xFF0288D1)
val ScreenBackground = Color(0xFFF8F9FA)
val CardBackground = Color.White
val SuccessGreen = Color(0xFF4CAF50)
val WarningRed = Color(0xFFE57373)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentProfileScreen(
    studentId: String,
    email: String,
    exam: String,
    onBackClick: () -> Unit,
    onSignOut: () -> Unit // Çıkış yapınca Login'e dönmek için
) {
    val viewModel = remember { StudentProfileViewModel() }
    var code by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    // Ekran açılınca profili (koç bilgisini) yükle
    LaunchedEffect(Unit) {
        viewModel.loadProfileData(studentId)
    }

    Scaffold(
        containerColor = ScreenBackground,
        topBar = {
            ProfileHeader(
                title = "Profil ve Ayarlar",
                onBackClick = onBackClick
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // 1. ÖĞRENCİ KİMLİK KARTI
            SectionTitle("Kişisel Bilgiler")
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profil Fotoğrafı Alanı (Placeholder)
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(BlueGradientEnd.copy(0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            null,
                            modifier = Modifier.size(32.dp),
                            tint = BlueGradientEnd
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = email,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            color = BlueGradientEnd.copy(0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Hedef: $exam",
                                style = MaterialTheme.typography.bodySmall,
                                color = BlueGradientEnd,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. KOÇ BAĞLANTISI ALANI
            SectionTitle("Eğitim Koçu Durumu")

            if (viewModel.connectedCoach != null) {
                // DURUM A: Zaten Bir Koça Bağlı
                ConnectedCoachCard(coach = viewModel.connectedCoach!!)
            } else {
                // DURUM B: Bağlı Değil -> Kod Girme Alanı
                NotConnectedCard(
                    code = code,
                    onCodeChange = { code = it },
                    isLoading = viewModel.isLoading,
                    isSuccess = viewModel.isSuccess,
                    statusMessage = viewModel.statusMessage,
                    onConnectClick = { viewModel.linkCoach(studentId, code) }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 3. ÇIKIŞ YAP BUTONU
            OutlinedButton(
                onClick = {
                    viewModel.signOut { onSignOut() }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = WarningRed
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, WarningRed),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.ExitToApp, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Hesaptan Çıkış Yap", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// --- YARDIMCI BİLEŞENLER ---

@Composable
fun ProfileHeader(title: String, onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(
                Brush.horizontalGradient(listOf(BlueGradientStart, BlueGradientEnd))
            )
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
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = Color.Gray,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
    )
}

@Composable
fun ConnectedCoachCard(coach: com.anlarsinsoftware.first_kmp_project.data.model.User) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)), // Açık Yeşil Arka Plan
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.School, null, tint = SuccessGreen)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Bağlı Koç",
                    style = MaterialTheme.typography.labelSmall,
                    color = SuccessGreen,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = coach.email, // İsim varsa coach.name
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.CheckCircle, null, tint = SuccessGreen)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotConnectedCard(
    code: String,
    onCodeChange: (String) -> Unit,
    isLoading: Boolean,
    isSuccess: Boolean,
    statusMessage: String?,
    onConnectClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Hesabınızı Bağlayın",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Koçunuzun size verdiği 6 haneli kodu girin.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Kod Giriş Alanı
            TextField(
                value = code,
                onValueChange = { onCodeChange(it.uppercase()) },
                placeholder = { Text("KOD: X9Y2Z1") },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Bağlan Butonu
            Button(
                onClick = onConnectClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = !isSuccess && !isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BlueGradientEnd)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(if (isSuccess) "Bağlantı Başarılı" else "Koçuma Bağlan")
                }
            }

            // Hata / Başarı Mesajı
            if (statusMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Warning,
                        null,
                        tint = if (isSuccess) SuccessGreen else WarningRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = statusMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSuccess) SuccessGreen else WarningRed
                    )
                }
            }
        }
    }
}