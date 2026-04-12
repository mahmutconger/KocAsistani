package com.anlarsinsoftware.first_kmp_project.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
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
import com.anlarsinsoftware.first_kmp_project.data.model.Assignment
import com.anlarsinsoftware.first_kmp_project.ui.components.ActivityHeatmap
import com.anlarsinsoftware.first_kmp_project.viewmodel.StudentDashboardViewModel





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDashboardScreen(
    userId: String,
    targetExamId: String,
    onAddStudyClick: () -> Unit,
    onProfileClick: () -> Unit,
    onChatClick: () -> Unit,
    onScheduleClick: () -> Unit,
    onFocusClick: () -> Unit
) {

// --- RENK PALETİ ---
    val BlueGradientStart = Color(0xFF4FC3F7)
    val BlueGradientEnd = Color(0xFF0288D1)
    val ScreenBackground = Color(0xFFF8F9FA)
    val CardBackground = Color.White
    val SuccessGreen = Color(0xFF4CAF50)

    val viewModel = remember { StudentDashboardViewModel() }
    val scrollState = rememberScrollState()

    // Bottom Sheet Kontrolü
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    // Ödevleri Filtrele
    val activeAssignments = viewModel.assignments.filter { !it.isCompleted }
    val completedAssignments = viewModel.assignments.filter { it.isCompleted }

    // Ekran açılınca verileri yükle
    LaunchedEffect(Unit) {
        viewModel.loadData(userId, targetExamId)
    }

    Scaffold(
        containerColor = ScreenBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddStudyClick,
                containerColor = BlueGradientEnd,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Çalışma Ekle")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {

            // 1. HEADER
            DashboardHeader(
                examName = viewModel.examName,
                daysLeft = viewModel.daysLeft,
                onProfileClick = onProfileClick,
                onChatClick = onChatClick
            )

            Column(modifier = Modifier.padding(16.dp)) {

                // 2. HIZLI ERİŞİM
                QuickActionCard(
                    title = "Haftalık Ders Programı",
                    subtitle = "Planını gör ve yönet",
                    icon = Icons.Default.DateRange,
                    onClick = onScheduleClick
                )

                Spacer(modifier = Modifier.height(12.dp))

                // B) ODAKLANMA MODU (YENİ KART)
                QuickActionCard(
                    title = "Odaklanma Modu",
                    subtitle = "Pomodoro ile verimli çalış",
                    icon = Icons.Default.PlayArrow,
                    onClick = onFocusClick
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 3. AKTİVİTE
                SectionTitle("Aktivite Durumu")
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        ActivityHeatmap(logs = viewModel.logs)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 4. ÖDEVLER (Sadece Bekleyenler)
                SectionTitle("Ödevlerim")

                if (activeAssignments.isEmpty() && completedAssignments.isEmpty()) {
                    EmptyHomeworkCard()
                } else {
                    // Bekleyen Ödevler Listesi
                    if (activeAssignments.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            activeAssignments.forEach { assignment ->
                                ModernAssignmentItem(
                                    assignment = assignment,
                                    onToggle = { viewModel.toggleAssignment(assignment) }
                                )
                            }
                        }
                    } else if (completedAssignments.isNotEmpty()) {
                        // Hiç aktif ödev yok ama tamamlananlar varsa
                        Text(
                            "Tüm ödevlerini bitirdin! 🎉",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    // "Tamamlananları Gör" Butonu
                    if (completedAssignments.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        TextButton(
                            onClick = { showBottomSheet = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Tamamlanan Ödevleri Gör (${completedAssignments.size})")
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.KeyboardArrowDown, null)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 5. MOTİVASYON
                SectionTitle("Günün Özeti")
                StatusCard(hasStudy = viewModel.logs.any { true })

                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // --- BOTTOM SHEET (Tamamlananlar) ---
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                // Sheet İçeriği
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 32.dp) // Alt navigasyon çubuğu için boşluk
                ) {
                    Text(
                        "Tamamlanan Ödevler",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Tamamlananlar Listesi
                    completedAssignments.forEach { assignment ->
                        ModernAssignmentItem(
                            assignment = assignment,
                            onToggle = {
                                // Buraya tıklandığında isCompleted false olacak
                                // ve anında ana listeye (bekleyenlere) geri dönecek
                                viewModel.toggleAssignment(assignment)
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (completedAssignments.isEmpty()) {
                        Text("Burada gösterilecek bir şey kalmadı.", color = Color.Gray)
                    }
                }
            }
        }
    }
}
// --- YARDIMCI BİLEŞENLER ---

@Composable
fun ModernAssignmentItem(assignment: Assignment, onToggle: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            // Tamamlandıysa hafif yeşil, değilse beyaz
            containerColor = if (assignment.isCompleted) Color(0xFFF1F8E9) else Color.White
        ),
        elevation = CardDefaults.cardElevation(if (assignment.isCompleted) 0.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier
                .clickable { onToggle() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = assignment.isCompleted,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = SuccessGreen,
                    checkmarkColor = Color.White
                )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = assignment.description,
                style = MaterialTheme.typography.bodyLarge,
                // Tamamlandıysa üstünü çiz
                textDecoration = if (assignment.isCompleted) TextDecoration.LineThrough else null,
                color = if (assignment.isCompleted) Color.Gray else Color.Black,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun DashboardHeader(
    examName: String,
    daysLeft: Long,
    onChatClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp) // Yüksek Header
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(BlueGradientStart, BlueGradientEnd)
                )
            )
    ) {
        // Arka plan desenleri (Opsiyonel süsleme)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 50.dp, y = (-50).dp)
                .size(200.dp)
                .background(Color.White.copy(alpha = 0.1f), CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // Üst Bar (Logo ve İkonlar)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Timofy",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = onChatClick,
                        modifier = Modifier.background(Color.White.copy(0.2f), CircleShape).size(40.dp)
                    ) {
                        Icon(Icons.Default.Email, null, tint = Color.White)
                    }
                    IconButton(
                        onClick = onProfileClick,
                        modifier = Modifier.background(Color.White.copy(0.2f), CircleShape).size(40.dp)
                    ) {
                        Icon(Icons.Default.Settings, null, tint = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Geri Sayım (Hero Content)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (examName.isNotEmpty()) "$examName Hedefi" else "Hedef Belirleniyor...",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )

                Text(
                    text = "$daysLeft",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 80.sp
                )

                Text(
                    text = "GÜN KALDI",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(BlueGradientEnd.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = BlueGradientEnd)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = Color.Gray,
        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
    )
}

@Composable
fun EmptyHomeworkCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)), // Çok açık mavi
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🎉", fontSize = 24.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                "Harika! Şu an yapman gereken bir ödev yok.",
                style = MaterialTheme.typography.bodyMedium,
                color = BlueGradientEnd
            )
        }
    }
}

@Composable
fun StatusCard(hasStudy: Boolean) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = if (hasStudy) "Bugün çalışmaya başladın, harika gidiyorsun!" else "Henüz bir çalışma girmedin. Küçük bir adımla başla!",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}