package com.example.tricommconnect_v1.ui.chatdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tricommconnect_v1.viewmodel.ChatDetailViewModel
import androidx.compose.runtime.livedata.observeAsState

@Composable
fun ChatDetailScreen(
    viewModel: ChatDetailViewModel,
    currentUserId: String
) {
    val messages by viewModel.messages.collectAsState()
    var newMessage by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            reverseLayout = true
        ) {
            items(messages.size) { index ->
                val msg = messages[messages.size - 1 - index] // reverse
                val isMe = msg.senderId == currentUserId
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                ) {
                    Text(
                        text = msg.content,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .padding(8.dp)
                            .background(
                                if (isMe) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.secondary
                            )
                            .padding(12.dp)
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = newMessage,
                onValueChange = { newMessage = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(12.dp)
            )

            Button(
                onClick = {
                    if (newMessage.isNotBlank()) {
                        viewModel.sendMessage(
                            content = newMessage,
                            senderId = currentUserId,
                            receiverId = "RECEIVER_ID_PLACEHOLDER" // to be dynamic in next cycles
                        )
                        newMessage = ""
                    }
                }
            ) {
                Text("Send")
            }
        }
    }
}
