package com.example.tricommconnect_v1.ui.chatlist

import androidx.compose.foundation.clickable
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
import androidx.navigation.NavController
import com.example.tricommconnect_v1.viewmodel.ChatListViewModel

// ui/chatlist/ChatListScreen.kt
@Composable
fun ChatListScreen(
    viewModel: ChatListViewModel,
    navController: NavController
) {
    val chats = viewModel.chatList // ✅ ViewModel exposes chatList state
    val loading = viewModel.loading
    val error = viewModel.error

    LaunchedEffect(Unit) {
        viewModel.loadChats() // ✅ Trigger data load on screen start
    }

    when {
        loading -> CircularProgressIndicator() // ✅ Show loading state
        error != null -> Text("Error: $error") // ✅ Show error if occurred
        else -> LazyColumn {
            items(chats) { chat ->
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable {
                            viewModel.userId?.let { userId ->
                                navController.navigate("chat/${chat._id}/$userId") // ✅ Navigate to ChatDetailScreen
                            }
                        }
                ) {
                    Text(chat.groupName, fontWeight = FontWeight.Bold)
                    Text(chat.lastMessage?.msgBody ?: "No messages yet", color = Color.Gray)
                }
            }
        }
    }
}
