package com.anlarsinsoftware.first_kmp_project.ui.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anlarsinsoftware.first_kmp_project.data.model.Role

@Composable
fun RoleSelectionScreen(
    onRoleSelected: (Role) -> Unit,
    isLoading: Boolean
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Devam etmeden önce...",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            "Hangi amaçla buradasın?",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            // Öğrenci Kartı
            RoleCard(
                title = "ÖĞRENCİYİM",
                description = "Sınavlara hazırlanıyorum, takibimi yapmak istiyorum.",
                onClick = { onRoleSelected(Role.STUDENT) },
                color = MaterialTheme.colorScheme.primaryContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Koç Kartı
            RoleCard(
                title = "EĞİTİM KOÇUYUM",
                description = "Öğrencilerimin ilerlemesini takip etmek istiyorum.",
                onClick = { onRoleSelected(Role.COACH) },
                color = MaterialTheme.colorScheme.secondaryContainer
            )
        }
    }
}

@Composable
fun RoleCard(title: String, description: String, onClick: () -> Unit, color: androidx.compose.ui.graphics.Color) {
    Card(
        modifier = Modifier.fillMaxWidth().height(150.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}