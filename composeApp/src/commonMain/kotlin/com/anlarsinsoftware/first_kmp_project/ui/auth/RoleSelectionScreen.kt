package com.anlarsinsoftware.first_kmp_project.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SupervisedUserCircle
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
import com.anlarsinsoftware.first_kmp_project.data.model.Role


@Composable
fun RoleSelectionScreen(
    onRoleSelected: (Role) -> Unit,
    isLoading: Boolean
) {

// --- RENK PALETİ ---
    val BlueGradientStart = Color(0xFF4FC3F7)
    val BlueGradientEnd = Color(0xFF0288D1)
    val ScreenBackground = Color(0xFFF8F9FA)
    // Animasyon durumunu yönetmek için state
    // initial = false, target = true olunca animasyon tetiklenir
    val visibleState = remember {
        MutableTransitionState(false).apply { targetState = true }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
    ) {
        // 1. MODERN HEADER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(bottomStart = 60.dp, bottomEnd = 60.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(BlueGradientStart, BlueGradientEnd)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (-30).dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Hoş Geldin!",
                    style = MaterialTheme.typography.displaySmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Devam etmek için kimliğini seç.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        // 2. KART ALANI (Animasyonlu)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp)) // Header'ın altı

            if (isLoading) {
                CircularProgressIndicator(color = BlueGradientEnd)
            } else {
                // Animasyon Bloğu
                AnimatedVisibility(
                    visibleState = visibleState,
                    enter = slideInVertically(
                        initialOffsetY = { 100 }, // 100px aşağıdan gelsin
                        animationSpec = tween(durationMillis = 600)
                    ) + fadeIn(animationSpec = tween(durationMillis = 600))
                ) {
                    Column {
                        // ÖĞRENCİ KARTI
                        RoleSelectionCard(
                            title = "Öğrenciyim",
                            description = "Derslerimi, hedeflerimi ve gelişimimi takip etmek istiyorum.",
                            icon = Icons.Default.School,
                            onClick = { onRoleSelected(Role.STUDENT) }
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // KOÇ KARTI
                        RoleSelectionCard(
                            title = "Eğitim Koçuyum",
                            description = "Öğrencilerime rehberlik edip programlarını yönetmek istiyorum.",
                            icon = Icons.Default.SupervisedUserCircle,
                            onClick = { onRoleSelected(Role.COACH) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RoleSelectionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable { onClick() }, // Tıklama efekti
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // İkon Kutusu
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(BlueGradientEnd.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = BlueGradientEnd,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Yazılar
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    lineHeight = 16.sp
                )
            }

            // Ok İşareti (Sağda)
            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}