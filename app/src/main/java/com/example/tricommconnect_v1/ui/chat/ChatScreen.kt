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
import com.example.tricommconnect_v1.viewmodel.MessageViewModel
import com.example.tricommconnect_v1.viewmodel.MessageListState // this line is modified/change
import com.example.tricommconnect_v1.viewmodel.RemoteMessageState
import com.example.tricommconnect_v1.viewmodel.RemoteMessageViewModel
import com.example.tricommconnect_v1.viewmodel.SendState


@Composable
fun ChatScreen(
    chatId: String,
    senderId: String,
    viewModel: MessageViewModel = viewModel()
) {
    // ✏️ Debug
    Text(text = "DEBUG: chatId = $chatId, senderId = $senderId")

    val listState by viewModel.messageListState.collectAsState()
    val sendState by viewModel.sendState.collectAsState()
    var messageText by remember { mutableStateOf("") }

    LaunchedEffect(chatId) { viewModel.observeMessages(chatId) }

    Column(Modifier.fillMaxSize()) {
        // Always show messages if we have them
        when (listState) {
            is MessageListState.Success -> {
                val msgs = (listState as MessageListState.Success).messages
                LazyColumn(
                    Modifier.weight(1f).padding(8.dp),
                    reverseLayout = true
                ) {
                    items(msgs.reversed()) { m ->
                        MessageBubble(
                            message = m,
                            isOwnMessage = m.senderUserId?._id == senderId
                        )
                    }
                }
            }
            is MessageListState.Loading -> {
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is MessageListState.Error -> {
                Text(
                    text = "Error loading: ${(listState as MessageListState.Error).error}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // Input row
        Row(
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier.weight(1f).background(MaterialTheme.colorScheme.surface).size(50.dp)
            )
            IconButton(onClick = {
                viewModel.sendMessage(chatId, senderId, messageText)
                messageText = ""
            }) {
                Icon(Icons.Filled.Send, contentDescription = "Send")
            }
        }

        // Show send‑status feedback (optional)
        when (sendState) {
            is SendState.Sending -> Text("Sending…", Modifier.padding(8.dp))
            is SendState.Error -> Text("error")
            else -> { /* Idle or Sent – nothing to show */ }
        }
    }

    // Reset send indicator once we’ve shown it
    LaunchedEffect(sendState) {
        if (sendState is SendState.Sent || sendState is SendState.Error) {
            viewModel.resetSendState()
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
                Text(
                    text = message.msgBody,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = message.time.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}
