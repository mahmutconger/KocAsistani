package com.anlarsinsoftware.first_kmp_project.ui.student


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anlarsinsoftware.first_kmp_project.ui.student.focus.FocusTimerViewModel
import com.anlarsinsoftware.first_kmp_project.ui.student.focus.TimerState
import com.anlarsinsoftware.first_kmp_project.ui.student.profile.BlueGradientEnd

@Composable
fun FocusTimerScreen(
    studentId: String,
    onCloseClick: () -> Unit
) {
    // ViewModel'i bir üst katmanda veya singleton tutmak daha iyi olabilir
    // ama şimdilik burada remember ile tutalım.
    val viewModel = remember { FocusTimerViewModel() }

    // Ayar Dialog'u
    var showSettings by remember { mutableStateOf(true) } // İlk açılışta sor

    if (showSettings) {
        TimerSettingsDialog(
            initialWork = viewModel.workDuration,
            initialBreak = viewModel.breakDuration,
            onConfirm = { work, breakTime ->
                viewModel.updateSettings(work, breakTime)
                showSettings = false
            },
            onDismiss = onCloseClick
        )
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Durum Başlığı
            Text(
                text = if (viewModel.currentState == TimerState.BREAK) "☕ MOLA ZAMANI" else "🔥 ODAKLAN",
                style = MaterialTheme.typography.headlineMedium,
                color = if (viewModel.currentState == TimerState.BREAK) Color(0xFF4CAF50) else BlueGradientEnd
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Dairesel Sayaç
            Box(contentAlignment = Alignment.Center) {
                CircularProgress(
                    progress = viewModel.timeLeftSeconds.toFloat() / (if (viewModel.currentState == TimerState.BREAK) viewModel.breakDuration * 60 else viewModel.workDuration * 60),
                    color = if (viewModel.currentState == TimerState.BREAK) Color(0xFF4CAF50) else BlueGradientEnd
                )
                Text(
                    text = viewModel.formattedTime,
                    fontSize = 64.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Kontrol Butonları
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                // Durdur/Bitir
                OutlinedButton(
                    onClick = {
                        viewModel.stopTimer()
                        onCloseClick()
                    },
                    modifier = Modifier.size(64.dp),
                    shape = androidx.compose.foundation.shape.CircleShape,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Default.Close, null)
                }

                // Başlat/Duraklat
                Button(
                    onClick = {
                        if (viewModel.currentState == TimerState.RUNNING) viewModel.pauseTimer()
                        else viewModel.startTimer(studentId)
                    },
                    modifier = Modifier.size(80.dp),
                    shape = androidx.compose.foundation.shape.CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = BlueGradientEnd)
                ) {
                    Icon(
                        if (viewModel.currentState == TimerState.RUNNING) Icons.Default.Pause else Icons.Default.PlayArrow,
                        null,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("Döngü: ${viewModel.currentCycle}", color = Color.Gray)
        }
    }
}

@Composable
fun CircularProgress(progress: Float, color: Color) {
    Canvas(modifier = Modifier.size(300.dp)) {
        // Arka plan halkası
        drawArc(
            color = Color.LightGray.copy(alpha = 0.3f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round)
        )
        // İlerleme halkası
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = 360 * progress,
            useCenter = false,
            style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
fun TimerSettingsDialog(
    initialWork: Int,
    initialBreak: Int,
    onConfirm: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var work by remember { mutableStateOf(initialWork.toString()) }
    var breakTime by remember { mutableStateOf(initialBreak.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Zamanlayıcı Ayarları") },
        text = {
            Column {
                OutlinedTextField(
                    value = work,
                    onValueChange = { work = it },
                    label = { Text("Çalışma Süresi (Dk)") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = breakTime,
                    onValueChange = { breakTime = it },
                    label = { Text("Mola Süresi (Dk)") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(work.toIntOrNull() ?: 25, breakTime.toIntOrNull() ?: 5)
            }) {
                Text("Başla")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("İptal") }
        }
    )
}