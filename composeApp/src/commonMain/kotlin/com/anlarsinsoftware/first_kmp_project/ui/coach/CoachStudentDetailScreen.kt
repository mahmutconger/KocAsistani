package com.anlarsinsoftware.first_kmp_project.ui.coach

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.anlarsinsoftware.first_kmp_project.data.model.DailyFocusSummary
import com.anlarsinsoftware.first_kmp_project.ui.components.ActivityHeatmap
import com.anlarsinsoftware.first_kmp_project.ui.components.StudyLogItem
import com.anlarsinsoftware.first_kmp_project.viewmodel.StudentDetailViewModel

// Renkler (Tutarlılık için)
val BlueGradientStart = Color(0xFF4FC3F7)
val BlueGradientEnd = Color(0xFF0288D1)
val ScreenBackground = Color(0xFFF8F9FA)
val SuccessGreen = Color(0xFF4CAF50)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoachStudentDetailScreen(
    coachId: String,
    studentId: String,
    onBackClick: () -> Unit,
    onChatClick: () -> Unit,
    onScheduleClick: () -> Unit
) {
    val viewModel = remember { StudentDetailViewModel() }

    // Hangi sekmedeyiz? (0: Genel, 1: Ödevler, 2: Odaklanma)
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Genel Bakış", "Ödev Takibi", "Odaklanma")

    // Ödev Atama Diyaloğu Kontrolü
    var showAssignDialog by remember { mutableStateOf(false) }
    var homeworkText by remember { mutableStateOf("") }

    LaunchedEffect(studentId) {
        viewModel.loadStudentData(studentId)
    }

    Scaffold(
        containerColor = ScreenBackground,
        topBar = {
            CoachDetailHeader(title = "Öğrenci Analizi", onBackClick = onBackClick)
        },
        floatingActionButton = {
            // Sadece Ödev sekmesindeyken "+" butonu göster, diğerlerinde Chat vs. olabilir
            if (selectedTab == 1) {
                FloatingActionButton(
                    onClick = { showAssignDialog = true },
                    containerColor = BlueGradientEnd,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, "Ödev Ata")
                }
            } else {
                FloatingActionButton(
                    onClick = onChatClick,
                    containerColor = BlueGradientEnd,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Email, "Mesaj")
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            // --- TAB BAR (Sekmeler) ---
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = BlueGradientEnd,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = BlueGradientEnd
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            // --- TAB İÇERİKLERİ ---
            when (selectedTab) {
                0 -> OverviewTab(
                    viewModel = viewModel,
                    onScheduleClick = onScheduleClick
                )
                1 -> AssignmentsTab(
                    assignments = viewModel.assignments
                )
                2 -> FocusAnalyticsTab(
                    summaries = viewModel.focusSummaries
                )
            }
        }

        // --- ÖDEV ATAMA DİYALOĞU ---
        if (showAssignDialog) {
            AlertDialog(
                onDismissRequest = { showAssignDialog = false },
                title = { Text("Yeni Ödev Ata") },
                text = {
                    OutlinedTextField(
                        value = homeworkText,
                        onValueChange = { homeworkText = it },
                        label = { Text("Ödev Açıklaması") },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        viewModel.assignHomework(coachId, studentId, homeworkText)
                        homeworkText = ""
                        showAssignDialog = false
                    }, colors = ButtonDefaults.buttonColors(containerColor = BlueGradientEnd)) {
                        Text("Ata")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAssignDialog = false }) { Text("İptal") }
                }
            )
        }
    }
}

// --- TAB 1: GENEL BAKIŞ ---
@Composable
fun OverviewTab(
    viewModel: StudentDetailViewModel,
    onScheduleClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Hızlı Aksiyonlar
        item {
            Text("İşlemler", style = MaterialTheme.typography.titleSmall, color = Color.Gray, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = onScheduleClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    elevation = ButtonDefaults.buttonElevation(2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.DateRange, null, tint = BlueGradientEnd)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Program Yaz", color = Color.Black)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Isı Haritası
        item {
            Text("Çalışma Yoğunluğu", style = MaterialTheme.typography.titleSmall, color = Color.Gray, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    ActivityHeatmap(logs = viewModel.logs)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Son Kayıtlar
        item {
            Text("Son Aktiviteler", style = MaterialTheme.typography.titleSmall, color = Color.Gray, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(viewModel.logs.take(10)) { log -> // Son 10 kayıt
            StudyLogItem(log = log)
        }
    }
}

// --- TAB 2: ÖDEV TAKİBİ ---
@Composable
fun AssignmentsTab(assignments: List<Assignment>) {
    val pending = assignments.filter { !it.isCompleted }
    val completed = assignments.filter { it.isCompleted }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Bekleyenler
        item {
            SectionHeader("Bekleyen Ödevler (${pending.size})", Icons.Default.Warning, Color(0xFFFFA726))
        }

        if (pending.isEmpty()) {
            item { EmptyListMessage("Bekleyen ödev yok.") }
        } else {
            items(pending) { assignment ->
                CoachAssignmentItem(assignment = assignment)
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // Tamamlananlar
        item {
            SectionHeader("Tamamlananlar (${completed.size})", Icons.Default.CheckCircle, SuccessGreen)
        }

        if (completed.isEmpty()) {
            item { EmptyListMessage("Henüz tamamlanan ödev yok.") }
        } else {
            items(completed) { assignment ->
                CoachAssignmentItem(assignment = assignment)
            }
        }
    }
}

// --- TAB 3: ODAKLANMA ANALİZİ ---
@Composable
fun FocusAnalyticsTab(summaries: List<DailyFocusSummary>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            // Toplam İstatistik Kartı
            val totalMinutes = summaries.sumOf { it.totalWorkMinutes }
            Card(
                colors = CardDefaults.cardColors(containerColor = BlueGradientEnd),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Son 7 Gün Toplam", color = Color.White.copy(0.8f))
                    Text(
                        "${totalMinutes / 60}sa ${totalMinutes % 60}dk",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text("Odaklanma Süresi", color = Color.White.copy(0.8f))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Text("Günlük Detaylar", style = MaterialTheme.typography.titleSmall, color = Color.Gray, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Basit Liste Halinde Gösterim (Grafik yerine daha detaylı liste)
        // Eğer grafik istersen previous turn'deki barlı yapıyı buraya da koyabiliriz.
        if (summaries.isEmpty()) {
            item { EmptyListMessage("Henüz odaklanma verisi yok.") }
        } else {
            items(summaries.sortedByDescending { it.date }) { summary ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(summary.date, fontWeight = FontWeight.Bold) // Tarih formatlanabilir
                            Text("${summary.sessionCount} Oturum", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                        Text(
                            "${summary.totalWorkMinutes} dk",
                            fontWeight = FontWeight.Bold,
                            color = BlueGradientEnd,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}

// --- YARDIMCI BİLEŞENLER ---

@Composable
fun CoachDetailHeader(title: String, onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(Brush.horizontalGradient(listOf(BlueGradientStart, BlueGradientEnd)))
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.background(Color.White.copy(0.2f), CircleShape).size(40.dp)
            ) {
                Icon(Icons.Default.ArrowBack, null, tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CoachAssignmentItem(assignment: Assignment) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if(assignment.isCompleted) Color(0xFFF1F8E9) else Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                if (assignment.isCompleted) Icons.Default.CheckCircle else Icons.Default.Info,
                null,
                tint = if (assignment.isCompleted) SuccessGreen else Color.Gray
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = assignment.description,
                style = MaterialTheme.typography.bodyMedium,
                textDecoration = if (assignment.isCompleted) TextDecoration.LineThrough else null,
                color = if (assignment.isCompleted) Color.Gray else Color.Black
            )
        }
    }
}

@Composable
fun SectionHeader(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
        Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun EmptyListMessage(text: String) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color.Gray, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
    }
}