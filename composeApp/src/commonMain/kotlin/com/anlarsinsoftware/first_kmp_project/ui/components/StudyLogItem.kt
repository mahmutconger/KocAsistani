package com.anlarsinsoftware.first_kmp_project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anlarsinsoftware.first_kmp_project.data.model.StudyLog
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

// --- RENK PALETİ ---
val BlueGradientEnd = Color(0xFF0288D1)
val SuccessGreen = Color(0xFF4CAF50)
val ErrorRed = Color(0xFFEF5350)
val CardWhite = Color.White

@Composable
fun StudyLogItem(log: StudyLog) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp), // Kartlar arası boşluk
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp), // Hafif gölge
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. İKON KUTUSU (Sol)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(BlueGradientEnd.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Book,
                    contentDescription = null,
                    tint = BlueGradientEnd,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 2. DERS BİLGİLERİ (Orta)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = log.lesson,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = log.topic,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Tarih
                Text(
                    text = formatTimestamp(log.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.LightGray
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 3. İSTATİSTİKLER (Sağ)
            Column(horizontalAlignment = Alignment.End) {
                // Toplam Soru
                Text(
                    text = "${log.count} Soru",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = BlueGradientEnd
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Doğru / Yanlış İndikatörleri
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatBadge(count = log.correct, color = SuccessGreen, icon = true)
                    Spacer(modifier = Modifier.width(8.dp))
                    StatBadge(count = log.wrong, color = ErrorRed, icon = false)
                }
            }
        }
    }
}

// Küçük Doğru/Yanlış Göstergesi
@Composable
fun StatBadge(count: Int, color: Color, icon: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (icon) Icons.Default.Check else Icons.Default.Close,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

// Tarih Formatlayıcı
@OptIn(ExperimentalTime::class)
fun formatTimestamp(timestamp: Timestamp): String {
    val instant = Instant.fromEpochSeconds(timestamp.seconds)
    val date = instant.toLocalDateTime(TimeZone.currentSystemDefault())

    // Örn: 16 Eki, 14:30 formatı
    // KMP'de java.time.format olmadığı için manuel string yapıyoruz
    // İstersen ay isimlerini mapleyebilirsin (1 -> Oca, 2 -> Şub...)
    return "${date.dayOfMonth}.${date.monthNumber}.${date.year} • ${date.hour}:${date.minute.toString().padStart(2, '0')}"
}