package com.example.tricommconnect_v1.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tricommconnect_v1.data.model.Message
import com.example.tricommconnect_v1.data.local.entity.MessageEntity
import com.example.tricommconnect_v1.data.local.mapper.toEntity
import com.example.tricommconnect_v1.data.local.mapper.toModel
import com.example.tricommconnect_v1.data.repository.MessageRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
// in viewmodel/MessageViewModel.kt

sealed class MessageListState {
    object Loading : MessageListState()
    data class Success(val messages: List<Message>) : MessageListState()
    data class Error(val error: String) : MessageListState()
}

sealed class SendState {
    object Idle : SendState()
    object Sending : SendState()
    object Sent : SendState()
    data class Error(val error: String) : SendState()
}

class MessageViewModel(
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val _messageListState = MutableStateFlow<MessageListState>(MessageListState.Loading)
    val messageListState: StateFlow<MessageListState> = _messageListState

    private val _sendState = MutableStateFlow<SendState>(SendState.Idle)
    val sendState: StateFlow<SendState> = _sendState

    fun observeMessages(chatId: String) {
        viewModelScope.launch {

            try {
                val remote = messageRepository.fetchRemoteMessages(chatId)
                Log.d("MessageViewModel", "Fetched ${remote.size} messages from API for chatId=$chatId")
                messageRepository.saveMessagesToLocal(chatId,remote)
            } catch (e: Exception) {
                Log.e("MessageViewModel", "API fetch failed", e)
                _messageListState.value = MessageListState.Error("Sync failed: ${e.message}")
            }

            _messageListState.value = MessageListState.Loading

            messageRepository
                .getLocalMessagesFlow(chatId)
                .map { list -> list.map { it.toModel() } }
                .onEach { models ->
                    Log.d(
                        "MessageViewModel",
                        "Room emitted ${models.size} messages (ids=${models.map{it._id}})"
                    )
                    _messageListState.value = MessageListState.Success(models)
                }
                .launchIn(viewModelScope)


            // Subscribe to local updates
            messageRepository.getLocalMessagesFlow(chatId)
                .map { list -> list.map { it.toModel() } }
                .onEach { _messageListState.value = MessageListState.Success(it) }
                .launchIn(viewModelScope)

            // Fetch remote once
            try {
                val remote = messageRepository.fetchRemoteMessages(chatId)
                messageRepository.saveMessagesToLocal(chatId,remote)
            } catch (e: Exception) {
                _messageListState.value = MessageListState.Error(e.message ?: "Sync failed")
            }
        }
    }

    fun sendMessage(chatId: String, senderId: String, msgBody: String) {
        viewModelScope.launch {
            _sendState.value = SendState.Sending
            try {
                val sent = messageRepository.sendMessage(chatId, senderId, msgBody)
                if (sent != null) {
                    _sendState.value = SendState.Sent
//                    // persist to local so list flow picks it up
//                    messageRepository.saveMessagesToLocal(listOf(sent))
                    // persist to local (with correct chatId) so list flow picks it up
                    messageRepository.saveMessagesToLocal(
                        chatId = chatId,
                        messages = listOf(sent)
                    )
                } else {
                    _sendState.value = SendState.Error("No response from server")
                }
            } catch (e: Exception) {
                _sendState.value = SendState.Error(e.message ?: "Send failed")
            }
        }
    }

    fun resetSendState() {
        _sendState.value = SendState.Idle
    }
}
