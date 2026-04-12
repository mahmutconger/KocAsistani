package com.anlarsinsoftware.first_kmp_project.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anlarsinsoftware.first_kmp_project.data.model.Message
import com.anlarsinsoftware.first_kmp_project.viewmodel.ChatViewModel

// --- RENK PALETİ ---
val BlueGradientStart = Color(0xFF4FC3F7)
val BlueGradientEnd = Color(0xFF0288D1)
val ScreenBackground = Color(0xFFF8F9FA)
val ChatBubbleWhite = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    currentUserId: String,
    coachId: String,
    studentId: String,
    chatTitle: String,
    onBackClick: () -> Unit
) {
    val viewModel = remember { ChatViewModel() }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.startListening(coachId, studentId)
    }

    LaunchedEffect(viewModel.messages.size) {
        if (viewModel.messages.isNotEmpty()) {
            listState.animateScrollToItem(viewModel.messages.size - 1)
        }
    }

    Scaffold(
        containerColor = ScreenBackground,
        topBar = {
            ChatHeader(title = chatTitle, onBackClick = onBackClick)
        },
        bottomBar = {
            // Klavye açılınca yukarı kayması için imePadding
            MessageInputArea(
                text = viewModel.messageText,
                onTextChanged = viewModel::onTextChanged,
                onSendClick = {
                    viewModel.sendMessage(coachId, studentId, currentUserId)
                }
            )
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp) // Mesajlar arası boşluk
        ) {
            items(viewModel.messages) { message ->
                MessageBubble(
                    message = message,
                    isMe = message.senderId == currentUserId
                )
            }
        }
    }
}

// --- MODERN HEADER ---
@Composable
fun ChatHeader(title: String, onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(
                Brush.horizontalGradient(listOf(BlueGradientStart, BlueGradientEnd))
            )
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    .size(40.dp)
            ) {
                Icon(Icons.Default.ArrowBack, "Geri", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                // Online durumu veya rol eklenebilir
                Text(
                    text = "Çevrimiçi",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

// --- PREMIUM BALONCUK TASARIMI ---
@Composable
fun MessageBubble(message: Message, isMe: Boolean) {
    val bubbleShape = if (isMe) {
        RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 2.dp)
    } else {
        RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 2.dp, bottomEnd = 20.dp)
    }

    val backgroundModifier = if (isMe) {
        Modifier.background(
            brush = Brush.horizontalGradient(listOf(BlueGradientStart, BlueGradientEnd)),
            shape = bubbleShape
        )
    } else {
        Modifier
            .shadow(2.dp, shape = bubbleShape)
            .background(ChatBubbleWhite, shape = bubbleShape)
    }

    val textColor = if (isMe) Color.White else Color.Black
    val align = if (isMe) Alignment.CenterEnd else Alignment.CenterStart

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = align
    ) {
        Box(
            modifier = backgroundModifier
                .widthIn(max = 300.dp) // Max genişlik
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = textColor
                )
                // Tarih/Saat alanı eklenebilir (Veri modelinde varsa)
                /*
                Text(
                    text = "14:30",
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor.copy(alpha = 0.7f),
                    modifier = Modifier.align(Alignment.End)
                )
                */
            }
        }
    }
}

// --- MODERN GİRİŞ ALANI ---
@Composable
fun MessageInputArea(
    text: String,
    onTextChanged: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Surface(
        color = Color.White,
        shadowElevation = 16.dp, // Yukarı doğru gölge
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .navigationBarsPadding() // Alt bar için padding
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Metin Kutusu
            TextField(
                value = text,
                onValueChange = onTextChanged,
                placeholder = { Text("Mesaj yaz...", color = Color.Gray) },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = ScreenBackground,
                    unfocusedContainerColor = ScreenBackground,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = BlueGradientEnd
                ),
                singleLine = false,
                maxLines = 3
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Gönder Butonu (Gradient)
            val isEnabled = text.isNotBlank()

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(
                        if (isEnabled) Brush.linearGradient(listOf(BlueGradientStart, BlueGradientEnd))
                        else Brush.linearGradient(listOf(Color.Gray, Color.Gray))
                    )
                    .clickable(enabled = isEnabled, onClick = onSendClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Gönder",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp).offset(x = 2.dp) // İkonu biraz ortalamak için
                )
            }
        }
    }
}