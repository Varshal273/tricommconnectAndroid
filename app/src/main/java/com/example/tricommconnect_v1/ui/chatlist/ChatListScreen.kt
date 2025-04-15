package com.example.tricommconnect_v1.ui.chatlist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tricommconnect_v1.viewmodel.ChatListViewModel

// ui/chatlist/ChatListScreen.kt
@Composable
fun ChatListScreen(viewModel: ChatListViewModel) {
    val chats = viewModel.chatList
    val loading = viewModel.loading
    val error = viewModel.error

    LaunchedEffect(Unit) {
        viewModel.loadChats()
    }

    when {
        loading -> CircularProgressIndicator()
        error != null -> Text("Error: $error")
        else -> LazyColumn {
            items(chats) { chat ->
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(chat.name, fontWeight = FontWeight.Bold)
                    Text(chat.lastMessage, color = Color.Gray)
                }
            }
        }
    }
}
