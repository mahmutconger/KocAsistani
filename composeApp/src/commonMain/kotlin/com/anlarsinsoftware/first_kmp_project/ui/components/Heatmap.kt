package com.anlarsinsoftware.first_kmp_project.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.anlarsinsoftware.first_kmp_project.data.model.StudyLog
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.datetime.*
import kotlin.time.ExperimentalTime

@Composable
fun ActivityHeatmap(logs: List<StudyLog>) {
    // KORUNAN MANTIK: Son 28 günü hesapla
    val today = Timestamp.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date

    val days = (0..27).map { today.minus(DatePeriod(days = 27 - it)) }

    // Hangi güne tıklandığını tutan state
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            "Son 4 Hafta Aktivitesi",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .height(180.dp) // Tooltip taşması için biraz yer açtık
                .padding(top = 8.dp), // Üst satırdaki tooltip kesilmesin diye padding
            userScrollEnabled = false // Sayfa scroll'u ile çakışmasın
        ) {
            items(days) { date ->
                // O güne ait toplam soru sayısını bul
                val totalSolvedThatDay = logs
                    .filter {
                        val logDate = it.timestamp.toLocalDateTime(TimeZone.currentSystemDefault()).date
                        logDate == date
                    }
                    .sumOf { it.count }

                // Hücre Bileşeni
                HeatmapCellContainer(
                    count = totalSolvedThatDay,
                    date = date,
                    isSelected = selectedDate == date,
                    onClick = {
                        // Zaten seçiliyse kapat, değilse onu seç
                        selectedDate = if (selectedDate == date) null else date
                    }
                )
            }
        }
    }
}

@Composable
fun HeatmapCellContainer(
    count: Int,
    date: LocalDate,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Renk mantığı aynı...
    val color = when {
        count == 0 -> Color(0xFFEEEEEE)
        count < 20 -> Color(0xFFC8E6C9)
        count < 50 -> Color(0xFF81C784)
        count < 100 -> Color(0xFF4CAF50)
        else -> Color(0xFF2E7D32)
    }

    Box(
        modifier = Modifier.aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        // --- BİLGİ BALONU (GÜNCELLENDİ) ---
        AnimatedVisibility(
            visible = isSelected,
            modifier = Modifier
                .zIndex(1f)
                .align(Alignment.TopCenter)
                // Balon büyüdüğü için daha yukarı itiyoruz (-45'ten -60'a çektik)
                .offset(y = (-60).dp),
            enter = slideInVertically(initialOffsetY = { 20 }) + fadeIn(),
            exit = fadeOut()
        ) {
            InfoBalloon(date = date, count = count)
        }

        // --- ASIL KUTUCUK ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(6.dp))
                .background(color)
                .clickable { onClick() }
        )
    }
}

@Composable
fun InfoBalloon(date: LocalDate, count: Int) {
    Surface(
        color = Color(0xFF263238), // Biraz daha koyu ve şık bir ton
        shape = RoundedCornerShape(12.dp), // Köşeleri daha yuvarlak yaptık
        shadowElevation = 8.dp, // Gölgeyi artırdık
        modifier = Modifier.widthIn(min = 80.dp) // Minimum genişlik verdik ki çok dar olmasın
    ) {
        Column(
            // Padding artırıldı (8dp -> 12dp ve 6dp -> 10dp)
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Tarih yazısı büyütüldü
            Text(
                text = "${date.dayOfMonth}.${date.monthNumber}.${date.year}",
                style = MaterialTheme.typography.bodySmall, // labelSmall'dan bodySmall'a
                color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Soru sayısı büyütüldü ve vurgulandı
            Text(
                text = "$count Soru",
                style = MaterialTheme.typography.titleMedium, // labelMedium'dan titleMedium'a (Bayağı büyüdü)
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }
    }
}
// Yardımcı Extension: Firebase Timestamp -> kotlinx.datetime.LocalDateTime
@OptIn(ExperimentalTime::class)
fun dev.gitlive.firebase.firestore.Timestamp.toLocalDateTime(timeZone: TimeZone): LocalDateTime {
    return Instant.fromEpochSeconds(this.seconds, this.nanoseconds.toLong())
        .toLocalDateTime(timeZone)
}