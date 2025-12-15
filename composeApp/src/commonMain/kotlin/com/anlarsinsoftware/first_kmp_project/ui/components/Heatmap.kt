package com.anlarsinsoftware.first_kmp_project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.anlarsinsoftware.first_kmp_project.data.model.StudyLog
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.datetime.*
import kotlin.time.ExperimentalTime

@Composable
fun ActivityHeatmap(logs: List<StudyLog>) {
    // Son 28 günü hesapla (4 hafta)
    // Clock.System yerine kotlinx.datetime.Clock.System kullanın
    val today = Timestamp.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date

    val days = (0..27).map { today.minus(DatePeriod(days = 27 - it)) }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text("Son 4 Hafta Aktivitesi", style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.height(160.dp)
        ) {
            items(days) { date ->
                // O güne ait toplam soru sayısını bul
                val totalSolvedThatDay = logs
                    .filter {
                        val logDate = it.timestamp.toLocalDateTime(TimeZone.currentSystemDefault()).date
                        logDate == date
                    }
                    .sumOf { it.count }

                HeatmapCell(count = totalSolvedThatDay, date = date)
            }
        }
    }
}

@Composable
fun HeatmapCell(count: Int, date: LocalDate) {
    val color = when {
        count == 0 -> Color(0xFFE0E0E0)
        count < 20 -> Color(0xFFC8E6C9)
        count < 50 -> Color(0xFF81C784)
        count < 100 -> Color(0xFF4CAF50)
        else -> Color(0xFF2E7D32)
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(4.dp))
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        // İsteğe bağlı: günü göster
    }
}

// Yardımcı Extension: Firebase Timestamp -> kotlinx.datetime.LocalDateTime
@OptIn(ExperimentalTime::class)
fun dev.gitlive.firebase.firestore.Timestamp.toLocalDateTime(timeZone: TimeZone): LocalDateTime {
    // Firebase Timestamp'i kotlinx.datetime.Instant'a çevir
    return Instant.fromEpochSeconds(this.seconds, this.nanoseconds.toLong())
        .toLocalDateTime(timeZone)
}