package com.anlarsinsoftware.first_kmp_project.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anlarsinsoftware.first_kmp_project.ui.student.profile.BlueGradientEnd

@Composable
fun FocusDashboardScreen(
    onBackClick: () -> Unit,
    onStartFocusClick: () -> Unit
) {
    Scaffold(
        topBar = {
            StudentScheduleHeader(title = "Odaklanma Modu", onBackClick = onBackClick)
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onStartFocusClick,
                containerColor = BlueGradientEnd,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.PlayArrow, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("POMODORO BAŞLAT")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 1. ÖZET KARTI
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Haftalık Odaklanma", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Basit Bar Grafiği (Placeholder)
                    // Gerçek veriyi repository'den çekip buraya çizeceğiz
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // Örnek Barlar (Pzt - Paz)
                        repeat(7) {
                            Box(
                                modifier = Modifier
                                    .width(20.dp)
                                    .fillMaxHeight((20..100).random() / 100f)
                                    .background(BlueGradientEnd, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("Pzt", "Sal", "Çar", "Per", "Cum", "Cmt", "Paz").forEach {
                            Text(it, fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Hazır olduğunda sağ alttaki butona basarak çalışmaya başla!",
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 32.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}