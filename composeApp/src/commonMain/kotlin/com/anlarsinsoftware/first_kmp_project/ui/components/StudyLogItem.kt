package com.anlarsinsoftware.first_kmp_project.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anlarsinsoftware.first_kmp_project.data.model.StudyLog
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

@Composable
fun StudyLogItem(log: StudyLog) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sol Taraf: Ders ve Konu
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = log.lesson,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = log.topic,
                    style = MaterialTheme.typography.bodyMedium
                )
                // Tarih (Basit format)
                Text(
                    text = formatTimestamp(log.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }

            // Sağ Taraf: İstatistik
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${log.count} Soru",
                    fontWeight = FontWeight.Bold
                )
                Row {
                    Text("${log.correct} D", color = Color(0xFF2E7D32), style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("${log.wrong} Y", color = Color(0xFFC62828), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

// Yardımcı Tarih Formatlayıcı
@OptIn(ExperimentalTime::class)
fun formatTimestamp(timestamp: Timestamp): String {
    val instant = Instant.fromEpochSeconds(timestamp.seconds)
    val date = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${date.dayOfMonth}/${date.monthNumber} ${date.hour}:${date.minute.toString().padStart(2, '0')}"
}