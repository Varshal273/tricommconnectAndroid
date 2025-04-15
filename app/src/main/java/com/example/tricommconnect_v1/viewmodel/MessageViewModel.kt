package com.example.tricommconnect_v1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tricommconnect_v1.data.local.entity.MessageEntity
import com.example.tricommconnect_v1.data.repository.MessageRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MessageViewModel(
    private val messageRepo: MessageRepository,
    private val chatId: String
) : ViewModel() {

    private val _messages = MutableStateFlow<List<MessageEntity>>(emptyList())
    val messages: StateFlow<List<MessageEntity>> = _messages

    init {
        observeMessages()
    }

    private fun observeMessages() {
        viewModelScope.launch {
            messageRepo.getMessagesForChat(chatId).collect {
                _messages.value = it
            }
        }
    }

    fun sendMessage(content: String, senderId: String, receiverId: String) {
        val message = MessageEntity(
            chatId = chatId,
            content = content,
            senderId = senderId,
            receiverId = receiverId,
            isSentByMe = true,
            timestamp = System.currentTimeMillis()
        )
        viewModelScope.launch {
            messageRepo.insertMessage(message)
        }
    }


    fun deleteAllMessagesInChat() {
        viewModelScope.launch {
            messageRepo.clearMessagesForChat(chatId)
        }
    }
}
