package com.anlarsinsoftware.first_kmp_project.ui.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- RENK PALETİ (Diğer ekranlarla uyumlu) ---
val BlueGradientStart = Color(0xFF4FC3F7)
val BlueGradientEnd = Color(0xFF0288D1)
val ScreenBackground = Color(0xFFF8F9FA)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LoginScreen(
    onAuthAction: (email: String, pass: String, isRegister: Boolean) -> Unit,
    isLoading: Boolean,
    error: String?
) {
    var isRegisterMode by remember { mutableStateOf(false) }

    // Arka Plan Rengi
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
    ) {
        // 1. HEADER (Gradient Arka Plan Süsü)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .clip(RoundedCornerShape(bottomStart = 60.dp, bottomEnd = 60.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(BlueGradientStart, BlueGradientEnd)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (-40).dp), // Biraz yukarı alalım
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Timofy",
                    style = MaterialTheme.typography.displayMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Akıllı Öğrenci Koçun",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        // 2. ANİMASYONLU KART ALANI
        // İçerik (Login veya Register) değiştikçe animasyon tetiklenir
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp)) // Header'ın altına gelmesi için boşluk

            AnimatedContent(
                targetState = isRegisterMode,
                transitionSpec = {
                    if (targetState) {
                        // Kayıt Moduna Geçerken: Sağdan gir, Sola çık
                        slideInHorizontally { width -> width } + fadeIn() with
                                slideOutHorizontally { width -> -width } + fadeOut()
                    } else {
                        // Giriş Moduna Dönerken: Soldan gir, Sağa çık
                        slideInHorizontally { width -> -width } + fadeIn() with
                                slideOutHorizontally { width -> width } + fadeOut()
                    }.using(
                        // Animasyon süresini yumuşat
                        sizeTransform = null
                    )
                },
                label = "AuthAnimation"
            ) { isRegister ->

                // --- FORM KARTI ---
                AuthCard(
                    isRegister = isRegister,
                    isLoading = isLoading,
                    error = error,
                    onAction = { email, pass -> onAuthAction(email, pass, isRegister) },
                    onSwitchMode = { isRegisterMode = !isRegisterMode }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthCard(
    isRegister: Boolean,
    isLoading: Boolean,
    error: String?,
    onAction: (String, String) -> Unit,
    onSwitchMode: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Başlık
            Text(
                text = if (isRegister) "Hesap Oluştur" else "Hoş Geldiniz",
                style = MaterialTheme.typography.headlineSmall,
                color = BlueGradientEnd,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isRegister) "Hemen aramıza katıl ve çalışmaya başla!" else "Devam etmek için giriş yapın.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // E-Posta Input
            AuthTextField(
                value = email,
                onValueChange = { email = it },
                label = "E-posta Adresi",
                icon = Icons.Default.Email,
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Şifre Input
            AuthTextField(
                value = password,
                onValueChange = { password = it },
                label = "Şifre",
                icon = Icons.Default.Lock,
                isPassword = true,
                isPasswordVisible = isPasswordVisible,
                onVisibilityChange = { isPasswordVisible = !isPasswordVisible }
            )

            // Hata Mesajı
            if (error != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Aksiyon Butonu (Gradient)
            Button(
                onClick = { onAction(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues() // Gradient'in tam dolması için
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(listOf(BlueGradientStart, BlueGradientEnd))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = if (isRegister) "KAYIT OL" else "GİRİŞ YAP",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mod Değiştirme Linki
            TextButton(onClick = onSwitchMode) {
                Text(
                    text = if (isRegister) "Zaten hesabın var mı? Giriş Yap" else "Hesabın yok mu? Hemen Kayıt Ol",
                    color = BlueGradientEnd,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onVisibilityChange: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = BlueGradientEnd) },
        trailingIcon = if (isPassword && onVisibilityChange != null) {
            {
                IconButton(onClick = onVisibilityChange) {
                    Icon(
                        if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Şifreyi Göster/Gizle",
                        tint = Color.Gray
                    )
                }
            }
        } else null,
        visualTransformation = if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BlueGradientEnd,
            unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
            focusedLabelColor = BlueGradientEnd,
            cursorColor = BlueGradientEnd
        ),
        singleLine = true
    )
}