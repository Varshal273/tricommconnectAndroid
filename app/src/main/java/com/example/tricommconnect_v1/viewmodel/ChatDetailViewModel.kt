package com.example.tricommconnect_v1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tricommconnect_v1.data.local.entity.MessageEntity
import com.example.tricommconnect_v1.data.repository.MessageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatDetailViewModel(
    private val messageRepo: MessageRepository,
    private val chatId: String, // chatId should be passed in to load messages for this specific chat
    private val senderId: String, // senderId will be passed to track who sent the message
    private val receiverId: String // receiverId will be passed to know who the message is sent to
) : ViewModel() {

    private val _messages = MutableStateFlow<List<MessageEntity>>(emptyList())
    val messages: StateFlow<List<MessageEntity>> = _messages

    init {
        loadMessages()
    }

    // Load all messages for the chatId
    private fun loadMessages() {
        viewModelScope.launch {
            messageRepo.getMessagesForChat(chatId).collect {
                _messages.value = it
            }
        }
    }

    // Send a new message
    fun sendMessage(content: String) {
        viewModelScope.launch {
            val message = MessageEntity(
                chatId = chatId,
                content = content,
                timestamp = System.currentTimeMillis(),
                senderId = senderId,
                receiverId = receiverId,
                isSentByMe = true // Assuming the current user is the sender
            )
            messageRepo.insertMessage(message)
        }
    }
}


//package com.example.tricommconnect_v1.viewmodel
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.tricommconnect_v1.data.local.entity.MessageEntity
//import com.example.tricommconnect_v1.data.repository.MessageRepository
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//
//class ChatDetailViewModel(
//    private val messageRepo: MessageRepository,
//    private val chatId: String
//) : ViewModel() {
//
//    private val _messages = MutableStateFlow<List<MessageEntity>>(emptyList())
//    val messages: StateFlow<List<MessageEntity>> = _messages
//
//    init {
//        loadMessages()
//    }
//
//    private fun loadMessages() {
//        viewModelScope.launch {
//            messageRepo.getMessagesForChat(chatId).collect {
//                _messages.value = it
//            }
//        }
//    }
//
//    fun sendMessage(content: String, senderId: String, receiverId: String) {
//        viewModelScope.launch {
//            val message = MessageEntity(
//                chatId = chatId,
//                content = content,
//                timestamp = System.currentTimeMillis(),
//                senderId = senderId,
//                receiverId = receiverId,
//                isSentByMe = true
//            )
//            messageRepo.insertMessage(message)
//        }
//    }
//}
