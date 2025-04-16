package com.example.tricommconnect_v1.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tricommconnect_v1.data.model.Message
import com.example.tricommconnect_v1.viewmodel.RemoteMessageState
import com.example.tricommconnect_v1.viewmodel.RemoteMessageViewModel

@Composable
fun ChatScreen(
    chatId: String,
    senderId: String,
    viewModel: RemoteMessageViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    var messageText by remember { mutableStateOf("") }

    LaunchedEffect(chatId) {
        viewModel.fetchMessages(chatId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        when (val s = state) {
            is RemoteMessageState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is RemoteMessageState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: ${s.error}")
                }
            }

            is RemoteMessageState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    reverseLayout = true
                ) {
                    items(s.messages.reversed()) { message ->
                        MessageBubble(message, isOwnMessage = message.senderUserId?._id == senderId)
                    }
                }
            }

            is RemoteMessageState.Sent -> {
                // Optionally trigger a UI update
                viewModel.fetchMessages(chatId)
                viewModel.resetState()
            }

            else -> {}
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            BasicTextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .heightIn(min = 48.dp),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.medium),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (messageText.isEmpty()) {
                            Text("Type a message...", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                        }
                        innerTextField()
                    }
                }
            )

            IconButton(onClick = {
                if (messageText.isNotBlank()) {
                    viewModel.sendMessage(chatId, senderId, messageText)
                    messageText = ""
                }
            }) {
                Icon(Icons.Default.Send, contentDescription = "Send")
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message, isOwnMessage: Boolean) {
    val alignment = if (isOwnMessage) Alignment.End else Alignment.Start
    val bubbleColor = if (isOwnMessage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isOwnMessage) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = bubbleColor,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = message.msgBody, color = MaterialTheme.colorScheme.onPrimary)
                Text(
                    text = message.time.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}
