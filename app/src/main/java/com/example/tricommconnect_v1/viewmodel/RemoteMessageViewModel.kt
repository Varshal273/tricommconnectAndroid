package com.example.tricommconnect_v1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tricommconnect_v1.data.model.Message
import com.example.tricommconnect_v1.data.remote.repository.RemoteMessageRepository
import com.example.tricommconnect_v1.data.local.dao.MessageDao
import com.example.tricommconnect_v1.data.local.mapper.toEntity
import com.example.tricommconnect_v1.data.local.mapper.toModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class RemoteMessageState {
    object Idle : RemoteMessageState()
    object Loading : RemoteMessageState()
    data class Success(val messages: List<Message>) : RemoteMessageState()
    data class Sent(val message: Message) : RemoteMessageState()
    data class Error(val error: String) : RemoteMessageState()
}

class RemoteMessageViewModel(
    private val remoteRepo: RemoteMessageRepository,
    private val messageDao: MessageDao
) : ViewModel() {

    private val _state = MutableStateFlow<RemoteMessageState>(RemoteMessageState.Idle)
    val state: StateFlow<RemoteMessageState> = _state

    fun fetchMessages(chatId: String) {
        viewModelScope.launch {
            _state.value = RemoteMessageState.Loading

            // 1. Observe local messages immediately (offline-first)
            messageDao.getMessagesForChat(chatId).collect { localMessages ->
                val messages = localMessages.map { it.toModel() }
                _state.value = RemoteMessageState.Success(messages)
            }
        }

        // 2. Sync from API and update Room
        viewModelScope.launch {
            try {
                val remoteMessages = remoteRepo.fetchMessages(chatId)
                if (remoteMessages.isNotEmpty()) {
                    messageDao.insertMessages(remoteMessages.map { it.toEntity() })
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Don't override UI — we already loaded from Room
            }
        }
    }

    fun sendMessage(chatId: String, senderId: String, message: String) {
        viewModelScope.launch {
            _state.value = RemoteMessageState.Loading
            try {
                val result = remoteRepo.sendMessage(chatId, senderId, message)
                if (result != null) {
                    messageDao.insertMessage(result.toEntity())
                    _state.value = RemoteMessageState.Sent(result)
                } else {
                    _state.value = RemoteMessageState.Error("Message response was null")
                }
            } catch (e: Exception) {
                _state.value = RemoteMessageState.Error(e.message ?: "Failed to send message")
            }
        }
    }

    fun resetState() {
        _state.value = RemoteMessageState.Idle
    }
}
